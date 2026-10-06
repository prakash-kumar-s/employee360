package com.employee360.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.employee360.dto.LeaveBalanceResponseDto;
import com.employee360.dto.LeaveRequestDto;
import com.employee360.dto.LeaveResponseDto;
import com.employee360.dto.LeaveTypeResponseDto;
import com.employee360.entity.LeaveRequest;
import com.employee360.entity.LeaveType;
import com.employee360.entity.User;
import com.employee360.repository.ApprovalStepRepository;
import com.employee360.repository.LeaveTypeRepository;
import com.employee360.repository.UserRepository;
import com.employee360.service.LeaveService;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveService leaveService;
    private final UserRepository userRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final ApprovalStepRepository approvalStepRepository;

    public LeaveController(
            LeaveService leaveService,
            UserRepository userRepository,
            LeaveTypeRepository leaveTypeRepository,
            ApprovalStepRepository approvalStepRepository) {

        this.leaveService = leaveService;
        this.userRepository = userRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.approvalStepRepository = approvalStepRepository;
    }

    @PostMapping
    public ResponseEntity<LeaveResponseDto> applyLeave(
            @RequestBody LeaveRequestDto request,
            Authentication authentication) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        User user =
                userRepository.findById(
                        request.getUserId()
                )
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "User not found."
                        )
                );

        if (!authenticatedUser.getId()
                .equals(user.getId())) {

            return ResponseEntity.status(403).build();
        }

        LeaveType leaveType =
                leaveTypeRepository.findById(
                        request.getLeaveTypeId()
                )
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Leave type not found."
                        )
                );

        LeaveRequest savedRequest =
                leaveService.applyLeave(
                        user,
                        leaveType,
                        request.getStartDate(),
                        request.getEndDate(),
                        request.getReason()
                );

        return ResponseEntity.ok(
                toLeaveResponseDto(savedRequest)
        );
    }

    @GetMapping("/types")
    public ResponseEntity<List<LeaveTypeResponseDto>> getLeaveTypes() {

        List<LeaveTypeResponseDto> response =
                leaveTypeRepository.findAll()
                        .stream()
                        .map(this::toLeaveTypeResponseDto)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LeaveResponseDto>> getLeaveHistory(
            @PathVariable Long userId,
            Authentication authentication) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        if (!authenticatedUser.getId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        User user =
                userRepository.findById(userId)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "User not found."
                                )
                        );

        List<LeaveResponseDto> response =
                leaveService
                        .getEmployeeLeaveHistory(user)
                        .stream()
                        .map(this::toLeaveResponseDto)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/balance/{userId}")
    public ResponseEntity<List<LeaveBalanceResponseDto>> getLeaveBalance(
            @PathVariable Long userId,
            Authentication authentication) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        if (!authenticatedUser.getId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        User user =
                userRepository.findById(userId)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "User not found."
                                )
                        );

        return ResponseEntity.ok(
                leaveService.getEmployeeLeaveBalances(user)
        );
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

    private LeaveResponseDto toLeaveResponseDto(
            LeaveRequest request) {

        LeaveResponseDto dto =
                new LeaveResponseDto();

        dto.setId(request.getId());

        dto.setUserId(
                request.getUser().getId()
        );

        dto.setUserName(
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

        dto.setRejectedByName(
                getRejectedByName(request)
        );

        dto.setStatus(
                request.getStatus()
        );

        return dto;
    }

    private String getRejectedByName(LeaveRequest request) {
        if (request.getRejectedByName() != null) {
            return request.getRejectedByName();
        }

        return approvalStepRepository
                .findByLeaveRequestOrderByStepOrder(request)
                .stream()
                .filter(step -> "REJECTED".equals(step.getStatus()))
                .map(step -> step.getApprover() == null
                        ? null
                        : step.getApprover().getName())
                .filter(name -> name != null)
                .findFirst()
                .orElse(null);
    }

    private LeaveTypeResponseDto toLeaveTypeResponseDto(
            LeaveType leaveType) {

        return new LeaveTypeResponseDto(
                leaveType.getId(),
                leaveType.getName(),
                leaveType.getEntitlement(),
                leaveType.getMaxConsecutiveLeave()
        );
    }
}