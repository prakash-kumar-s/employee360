package com.employee360.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import com.employee360.dto.ApprovalResponseDto;
import com.employee360.entity.ApprovalStep;
import com.employee360.entity.LeaveRequest;
import com.employee360.entity.User;
import com.employee360.repository.LeaveRequestRepository;
import com.employee360.repository.UserRepository;
import com.employee360.service.ApprovalService;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;
    private final UserRepository userRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public ApprovalController(
            ApprovalService approvalService,
            UserRepository userRepository,
            LeaveRequestRepository leaveRequestRepository) {

        this.approvalService = approvalService;
        this.userRepository = userRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<ApprovalResponseDto>> getPendingApprovals(
            @PathVariable Long managerId,
            Authentication authentication) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        if (!authenticatedUser.getId().equals(managerId)) {
            return ResponseEntity.status(403).build();
        }

        List<ApprovalStep> steps =
                approvalService.getPendingApprovals(
                        authenticatedUser
                );

        List<ApprovalResponseDto> response =
                steps.stream()
                        .map(this::toApprovalResponseDto)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{requestId}/approve")
    public ResponseEntity<ApprovalResponseDto> approveLeave(
            @PathVariable Long requestId,
            Authentication authentication) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        LeaveRequest leaveRequest =
                leaveRequestRepository.findById(requestId)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Leave request not found."
                                )
                        );

        LeaveRequest approvedRequest =
                approvalService.approveLeave(
                        requestId,
                        authenticatedUser
                );

        ApprovalStep currentStep =
                approvalService.getApprovalSteps(
                        approvedRequest
                )
                .stream()
                .filter(step ->
                        step.getApprover() != null
                                && step.getApprover()
                                        .getId()
                                        .equals(authenticatedUser.getId())
                )
                .reduce((first, second) -> second)
                .orElse(null);

        return ResponseEntity.ok(
                toApprovalResponseDto(currentStep)
        );
    }

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<ApprovalResponseDto> rejectLeave(
            @PathVariable Long requestId,
            @RequestBody RejectRequest request,
            Authentication authentication) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        LeaveRequest rejectedRequest =
                approvalService.rejectLeave(
                        requestId,
                        authenticatedUser,
                        request.rejectionReason()
                );

        ApprovalStep rejectedStep =
                approvalService.getApprovalSteps(
                        rejectedRequest
                )
                .stream()
                .filter(step ->
                        step.getApprover() != null
                                && step.getApprover()
                                        .getId()
                                        .equals(authenticatedUser.getId())
                                && "REJECTED".equals(
                                        step.getStatus()
                                )
                )
                .findFirst()
                .orElse(null);

        return ResponseEntity.ok(
                toApprovalResponseDto(rejectedStep)
        );
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<List<ApprovalResponseDto>> getApprovalSteps(
            @PathVariable Long requestId) {

        LeaveRequest leaveRequest =
                leaveRequestRepository.findById(requestId)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Leave request not found."
                                )
                        );

        List<ApprovalResponseDto> response =
                approvalService.getApprovalSteps(
                        leaveRequest
                )
                .stream()
                .map(this::toApprovalResponseDto)
                .toList();

        return ResponseEntity.ok(response);
    }

    private User getAuthenticatedUser(
            Authentication authentication) {

        String username =
                authentication.getName();

        User user =
                userRepository.findByUsername(username);

        if (user == null) {
            throw new IllegalArgumentException(
                    "Authenticated user not found."
            );
        }

        return user;
    }

    private ApprovalResponseDto toApprovalResponseDto(
            ApprovalStep step) {

        if (step == null) {
            return null;
        }

        LeaveRequest request =
                step.getLeaveRequest();

        ApprovalResponseDto dto =
                new ApprovalResponseDto();

        dto.setRequestId(request.getId());

        dto.setEmployeeId(
                request.getUser().getId()
        );

        dto.setEmployeeName(
                request.getUser().getName()
        );

        dto.setLeaveTypeId(
                request.getLeaveType().getId()
        );

        dto.setLeaveTypeName(
                request.getLeaveType().getName()
        );

        dto.setStartDate(
                request.getStartDate()
        );

        dto.setEndDate(
                request.getEndDate()
        );

        dto.setReason(
                request.getReason()
        );

        dto.setRejectionReason(
                request.getRejectionReason()
        );

        dto.setRequestStatus(
                request.getStatus()
        );

        dto.setApprovalStepId(
                step.getId()
        );

        dto.setStepOrder(
                step.getStepOrder()
        );

        dto.setApproverRole(
                step.getApproverRole()
        );

        dto.setApprovalStatus(
                step.getStatus()
        );

        return dto;
    }

    public record RejectRequest(
            String rejectionReason
    ) {
    }
}