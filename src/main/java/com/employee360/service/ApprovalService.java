package com.employee360.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employee360.entity.ApprovalStep;
import com.employee360.entity.LeaveBalance;
import com.employee360.entity.LeaveRequest;
import com.employee360.entity.User;
import com.employee360.repository.ApprovalStepRepository;
import com.employee360.repository.LeaveBalanceRepository;
import com.employee360.repository.LeaveRequestRepository;

@Service
public class ApprovalService {

    private final ApprovalStepRepository approvalStepRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;
    private final PolicyService policyService;

    public ApprovalService(
            ApprovalStepRepository approvalStepRepository,
            LeaveRequestRepository leaveRequestRepository,
            LeaveBalanceRepository leaveBalanceRepository,
            AuditService auditService,
            NotificationService notificationService,
            PolicyService policyService) {

        this.approvalStepRepository = approvalStepRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.policyService = policyService;
    }

    public List<ApprovalStep> getApprovalSteps(
            LeaveRequest leaveRequest) {

        return approvalStepRepository
                .findByLeaveRequestOrderByStepOrder(
                        leaveRequest
                );
    }

    public List<ApprovalStep> getPendingApprovals(
            User approver) {

        return approvalStepRepository
                .findByApproverAndStatus(
                        approver,
                        "PENDING"
                );
    }

    @Transactional
    public LeaveRequest approveLeave(
            Long requestId,
            User approver) {

        LeaveRequest leaveRequest =
                leaveRequestRepository.findById(requestId)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Leave request not found."
                                )
                        );

        ApprovalStep currentStep =
                approvalStepRepository
                        .findByLeaveRequestAndApproverAndStatus(
                                leaveRequest,
                                approver,
                                "PENDING"
                        );

        if (currentStep == null) {
            throw new IllegalArgumentException(
                    "No pending approval step found for this approver."
            );
        }

        String previousStepStatus =
                currentStep.getStatus();

        currentStep.setStatus("APPROVED");

        approvalStepRepository.save(currentStep);

        List<ApprovalStep> steps =
                approvalStepRepository
                        .findByLeaveRequestOrderByStepOrder(
                                leaveRequest
                        );

        ApprovalStep nextStep = null;

        for (ApprovalStep step : steps) {

            if (step.getStepOrder()
                    > currentStep.getStepOrder()
                    && "WAITING".equals(step.getStatus())) {

                nextStep = step;
                break;
            }
        }

        if (nextStep != null) {

            nextStep.setStatus("PENDING");

            approvalStepRepository.save(nextStep);

            notificationService.createNotification(
                    nextStep.getApprover(),
                    leaveRequest,
                    "APPROVAL_REQUIRED",
                    "A leave request is waiting for your approval."
            );

            auditService.recordAction(
                    approver,
                    leaveRequest,
                    "APPROVED_STEP",
                    previousStepStatus,
                    "APPROVED"
            );

            return leaveRequest;
        }

        return completeFinalApproval(
                leaveRequest,
                approver,
                previousStepStatus
        );
    }

    @Transactional
    public LeaveRequest rejectLeave(
            Long requestId,
            User approver,
            String rejectionReason) {

        LeaveRequest leaveRequest =
                leaveRequestRepository.findById(requestId)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Leave request not found."
                                )
                        );

        ApprovalStep currentStep =
                approvalStepRepository
                        .findByLeaveRequestAndApproverAndStatus(
                                leaveRequest,
                                approver,
                                "PENDING"
                        );

        if (currentStep == null) {
            throw new IllegalArgumentException(
                    "No pending approval step found for this approver."
            );
        }

        String previousStatus =
                leaveRequest.getStatus();

        currentStep.setStatus("REJECTED");

        approvalStepRepository.save(currentStep);

        leaveRequest.setStatus("REJECTED");
        leaveRequest.setRejectionReason(rejectionReason);

        leaveRequestRepository.save(leaveRequest);

        auditService.recordAction(
                approver,
                leaveRequest,
                "REJECTED",
                previousStatus,
                "REJECTED"
        );

        notificationService.createNotification(
                leaveRequest.getUser(),
                leaveRequest,
                "LEAVE_REJECTED",
                "Your leave request has been rejected."
        );

        return leaveRequest;
    }

    private LeaveRequest completeFinalApproval(
            LeaveRequest leaveRequest,
            User approver,
            String previousStepStatus) {

        String previousStatus =
                leaveRequest.getStatus();

        long leaveDays =
                policyService.calculateWorkingDays(
                        leaveRequest.getStartDate(),
                        leaveRequest.getEndDate()
                );

        LeaveBalance leaveBalance =
                leaveBalanceRepository.findByUserAndLeaveType(
                        leaveRequest.getUser(),
                        leaveRequest.getLeaveType()
                );

        if (leaveBalance == null) {
            throw new IllegalStateException(
                    "Leave balance not found."
            );
        }

        if (leaveBalance.getRemainingLeaves()
                < leaveDays) {

            throw new IllegalStateException(
                    "Insufficient remaining leave balance."
            );
        }

        leaveRequest.setStatus("APPROVED");

        leaveBalance.setUsedLeaves(
                leaveBalance.getUsedLeaves()
                        + (int) leaveDays
        );

        leaveBalance.setRemainingLeaves(
                leaveBalance.getRemainingLeaves()
                        - (int) leaveDays
        );

        leaveBalanceRepository.save(leaveBalance);

        leaveRequestRepository.save(leaveRequest);

        auditService.recordAction(
                approver,
                leaveRequest,
                "FINAL_APPROVAL",
                previousStatus,
                "APPROVED"
        );

        notificationService.createNotification(
                leaveRequest.getUser(),
                leaveRequest,
                "LEAVE_APPROVED",
                "Your leave request has been fully approved."
        );

        return leaveRequest;
    }
}