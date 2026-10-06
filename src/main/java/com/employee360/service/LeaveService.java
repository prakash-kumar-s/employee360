package com.employee360.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employee360.dto.LeaveBalanceResponseDto;
import com.employee360.engine.ApprovalEngine;
import com.employee360.entity.LeaveBalance;
import com.employee360.entity.LeaveRequest;
import com.employee360.entity.LeaveType;
import com.employee360.entity.User;
import com.employee360.repository.LeaveBalanceRepository;
import com.employee360.repository.LeaveRequestRepository;

@Service
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final PolicyService policyService;
    private final ApprovalEngine approvalEngine;

    public LeaveService(
            LeaveRequestRepository leaveRequestRepository,
            LeaveBalanceRepository leaveBalanceRepository,
            PolicyService policyService,
            ApprovalEngine approvalEngine) {

        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.policyService = policyService;
        this.approvalEngine = approvalEngine;
    }

    @Transactional
    public LeaveRequest applyLeave(
            User user,
            LeaveType leaveType,
            LocalDate startDate,
            LocalDate endDate,
            String reason) {

        List<String> errors =
                policyService.validateLeaveRequest(
                        user,
                        leaveType,
                        startDate,
                        endDate
                );

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(
                    String.join("; ", errors)
            );
        }

        long workingDays =
                policyService.calculateWorkingDays(
                        startDate,
                        endDate
                );

        LeaveRequest leaveRequest =
                new LeaveRequest();

        leaveRequest.setUser(user);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDate(startDate);
        leaveRequest.setEndDate(endDate);
        leaveRequest.setReason(reason);
        leaveRequest.setStatus("PENDING");

        LeaveRequest savedRequest =
                leaveRequestRepository.save(
                        leaveRequest
                );

        approvalEngine.createApprovalSteps(
                savedRequest,
                (int) workingDays
        );

        return savedRequest;
    }

    public List<LeaveRequest> getEmployeeLeaveHistory(
            User user) {

        return leaveRequestRepository
                .findByUserAndStatusIn(
                        user,
                        List.of(
                                "PENDING",
                                "APPROVED",
                                "REJECTED"
                        )
                );
    }

    public List<LeaveBalanceResponseDto> getEmployeeLeaveBalances(
            User user) {

        return leaveBalanceRepository
                .findByUser(user)
                .stream()
                .map(this::toBalanceDto)
                .toList();
    }

    private LeaveBalanceResponseDto toBalanceDto(
            LeaveBalance balance) {

        LeaveBalanceResponseDto dto =
                new LeaveBalanceResponseDto();

        dto.setId(balance.getId());

        dto.setUserId(
                balance.getUser().getId()
        );

        dto.setLeaveTypeId(
                balance.getLeaveType().getId()
        );

        dto.setLeaveTypeName(
                balance.getLeaveType().getName()
        );

        dto.setTotalLeaves(
                balance.getTotalLeaves()
        );

        dto.setUsedLeaves(
                balance.getUsedLeaves()
        );

        dto.setRemainingLeaves(
                balance.getRemainingLeaves()
        );

        return dto;
    }
}