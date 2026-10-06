package com.employee360.engine;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.employee360.entity.ApprovalStep;
import com.employee360.entity.LeaveRequest;
import com.employee360.entity.User;
import com.employee360.entity.WorkflowRule;
import com.employee360.repository.ApprovalStepRepository;
import com.employee360.repository.UserRepository;
import com.employee360.repository.WorkflowRuleRepository;

@Service
public class ApprovalEngine {

    private final WorkflowRuleRepository workflowRuleRepository;
    private final ApprovalStepRepository approvalStepRepository;
    private final UserRepository userRepository;

    public ApprovalEngine(
            WorkflowRuleRepository workflowRuleRepository,
            ApprovalStepRepository approvalStepRepository,
            UserRepository userRepository) {

        this.workflowRuleRepository = workflowRuleRepository;
        this.approvalStepRepository = approvalStepRepository;
        this.userRepository = userRepository;
    }

    public void createApprovalSteps(
            LeaveRequest leaveRequest,
            int workingDays) {

        List<WorkflowRule> matchingRules = workflowRuleRepository
                .findAll()
                .stream()
                .filter(rule -> !"ADMIN".equalsIgnoreCase(rule.getApproverRole()))
                .filter(rule -> workingDays >= rule.getMinDays())
                .filter(rule ->
                        rule.getMaxDays() == null
                                || workingDays <= rule.getMaxDays())
                .sorted(Comparator
                        .comparing(WorkflowRule::getApprovalLevel)
                        .thenComparing(WorkflowRule::getId))
                .toList();

        if (matchingRules.isEmpty()) {
            throw new RuntimeException(
                    "No approval workflow configured for "
                            + workingDays + " working days.");
        }

        int stepOrder = 1;

        for (WorkflowRule rule : matchingRules) {

            User approver = findApprover(
                    leaveRequest.getUser(),
                    rule.getApproverRole());

            if (approver == null) {
                throw new RuntimeException(
                        "No approver found for role: "
                                + rule.getApproverRole()
                                + " in the employee hierarchy.");
            }

            ApprovalStep step = new ApprovalStep();

            step.setStepOrder(stepOrder);
            step.setApproverRole(rule.getApproverRole());

            if (stepOrder == 1) {
                step.setStatus("PENDING");
            } else {
                step.setStatus("WAITING");
            }

            step.setLeaveRequest(leaveRequest);
            step.setApprover(approver);

            approvalStepRepository.save(step);

            stepOrder++;
        }
    }

    private User findApprover(
            User employee,
            String approverRole) {

        List<User> candidates = new ArrayList<>();

        if ("MANAGER".equalsIgnoreCase(approverRole)) {
            addCandidate(candidates, employee.getManager());
            addDepartmentCandidates(candidates, employee, "MANAGER");
            addDepartmentCandidates(candidates, employee, "DEPARTMENT_HEAD");
            candidates.addAll(userRepository.findByRole("MANAGER"));
            candidates.addAll(userRepository.findByRole("HR"));
            return firstOtherUser(employee, candidates);
        }

        if ("DEPARTMENT_HEAD".equalsIgnoreCase(approverRole)) {
            addDepartmentCandidates(candidates, employee, "DEPARTMENT_HEAD");
            addDepartmentCandidates(candidates, employee, "MANAGER");
            addCandidate(candidates, employee.getManager());
            candidates.addAll(userRepository.findByRole("HR"));
            return firstOtherUser(employee, candidates);
        }

        if ("HR".equalsIgnoreCase(approverRole)) {
            candidates.addAll(userRepository.findByRole("HR"));
            addDepartmentCandidates(candidates, employee, "DEPARTMENT_HEAD");
            addCandidate(candidates, employee.getManager());
            return firstOtherUser(employee, candidates);
        }

        return firstOtherUser(employee, userRepository.findByRole(approverRole));
    }

    private void addDepartmentCandidates(
            List<User> candidates,
            User employee,
            String role) {

        if (employee.getDepartment() != null) {
            candidates.addAll(
                    userRepository.findByDepartmentAndRole(employee.getDepartment(), role));
        }
    }

    private void addCandidate(List<User> candidates, User candidate) {
        if (candidate != null) {
            candidates.add(candidate);
        }
    }

    private User firstOtherUser(User employee, List<User> candidates) {
        return candidates.stream()
                .filter(candidate -> !Objects.equals(candidate.getId(), employee.getId()))
                .findFirst()
                .orElse(null);
    }
}