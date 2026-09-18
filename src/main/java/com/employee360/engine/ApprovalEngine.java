package com.employee360.engine;

import java.util.Comparator;
import java.util.List;

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

        if ("MANAGER".equalsIgnoreCase(approverRole)) {

            return employee.getManager();
        }

        if ("DEPARTMENT_HEAD".equalsIgnoreCase(approverRole)) {

            if (employee.getDepartment() == null) {
                return null;
            }

            List<User> departmentHeads =
                    userRepository.findByDepartmentAndRole(
                            employee.getDepartment(),
                            "DEPARTMENT_HEAD");

            if (departmentHeads.isEmpty()) {
                return null;
            }

            return departmentHeads.get(0);
        }

        if ("HR".equalsIgnoreCase(approverRole)) {

            List<User> hrUsers =
                    userRepository.findByRole("HR");

            if (hrUsers.isEmpty()) {
                return null;
            }

            return hrUsers.get(0);
        }

        List<User> users =
                userRepository.findByRole(approverRole);

        if (users.isEmpty()) {
            return null;
        }

        return users.get(0);
    }
}