package com.employee360.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee360.entity.ApprovalStep;
import com.employee360.entity.LeaveRequest;
import com.employee360.entity.User;

public interface ApprovalStepRepository
        extends JpaRepository<ApprovalStep, Long> {

    List<ApprovalStep> findByLeaveRequestOrderByStepOrder(
            LeaveRequest leaveRequest
    );

    ApprovalStep findByLeaveRequestAndApproverAndStatus(
            LeaveRequest leaveRequest,
            User approver,
            String status
    );

    List<ApprovalStep> findByApproverAndStatus(
            User approver,
            String status
    );

    List<ApprovalStep> findByApprover(
            User approver
    );
}