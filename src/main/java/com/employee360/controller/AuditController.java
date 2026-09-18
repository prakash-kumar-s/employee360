package com.employee360.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.employee360.dto.AuditResponseDto;
import com.employee360.dto.LeaveResponseDto;
import com.employee360.entity.LeaveRequest;
import com.employee360.entity.User;
import com.employee360.repository.LeaveRequestRepository;
import com.employee360.repository.UserRepository;
import com.employee360.service.AuditService;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditService auditService;
    private final LeaveRequestRepository leaveRequestRepository;
    private final UserRepository userRepository;

    public AuditController(
            AuditService auditService,
            LeaveRequestRepository leaveRequestRepository,
            UserRepository userRepository) {

        this.auditService =
                auditService;

        this.leaveRequestRepository =
                leaveRequestRepository;

        this.userRepository =
                userRepository;
    }

    @GetMapping("/requests")
    public ResponseEntity<List<LeaveResponseDto>> getAuditableRequests(
            Authentication authentication) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        List<LeaveRequest> requests =
                auditService.getAuditableRequests(authenticatedUser);

        List<LeaveResponseDto> dtos =
                requests.stream()
                        .map(this::toLeaveResponseDto)
                        .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/request/{requestId}")
    public ResponseEntity<List<AuditResponseDto>> getRequestAudit(
            @PathVariable Long requestId,
            Authentication authentication) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        LeaveRequest leaveRequest =
                leaveRequestRepository
                        .findById(requestId)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Leave request not found."
                                )
                        );

        /*
         * Employees can view audit history
         * for their own leave requests.
         */
        if (leaveRequest
                .getUser()
                .getId()
                .equals(authenticatedUser.getId())) {

            return ResponseEntity.ok(
                    auditService.getLeaveRequestAudit(
                            leaveRequest
                    )
            );
        }

        /*
         * Managers, HR and ADMIN users are allowed
         * to access the request audit information.
         */
        String role =
                authenticatedUser.getRole();

        if ("MANAGER".equalsIgnoreCase(role)
                || "DEPARTMENT_HEAD".equalsIgnoreCase(role)
                || "HR".equalsIgnoreCase(role)
                || "ADMIN".equalsIgnoreCase(role)) {

            return ResponseEntity.ok(
                    auditService.getLeaveRequestAudit(
                            leaveRequest
                    )
            );
        }

        return ResponseEntity.status(403).build();
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

    private LeaveResponseDto toLeaveResponseDto(LeaveRequest request) {
        LeaveResponseDto dto = new LeaveResponseDto();
        dto.setId(request.getId());
        if (request.getUser() != null) {
            dto.setUserId(request.getUser().getId());
            dto.setUserName(request.getUser().getName());
        }
        if (request.getLeaveType() != null) {
            dto.setLeaveTypeId(request.getLeaveType().getId());
            dto.setLeaveTypeName(request.getLeaveType().getName());
        }
        dto.setStartDate(request.getStartDate());
        dto.setEndDate(request.getEndDate());
        dto.setReason(request.getReason());
        dto.setRejectionReason(request.getRejectionReason());
        dto.setStatus(request.getStatus());
        return dto;
    }
}