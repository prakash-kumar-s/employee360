package com.employee360.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.employee360.dto.AuditResponseDto;
import com.employee360.entity.ApprovalStep;
import com.employee360.entity.AuditLog;
import com.employee360.entity.LeaveRequest;
import com.employee360.entity.User;
import com.employee360.repository.ApprovalStepRepository;
import com.employee360.repository.AuditLogRepository;
import com.employee360.repository.LeaveRequestRepository;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final ApprovalStepRepository approvalStepRepository;

    public AuditService(
            AuditLogRepository auditLogRepository,
            LeaveRequestRepository leaveRequestRepository,
            ApprovalStepRepository approvalStepRepository) {

        this.auditLogRepository = auditLogRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.approvalStepRepository = approvalStepRepository;
    }

    public List<LeaveRequest> getAuditableRequests(User user) {
        String role = user.getRole();
        if ("HR".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
            return leaveRequestRepository.findAll();
        } else if ("MANAGER".equalsIgnoreCase(role) || "DEPARTMENT_HEAD".equalsIgnoreCase(role)) {
            Set<LeaveRequest> requests = new LinkedHashSet<>(leaveRequestRepository.findByUser(user));
            List<ApprovalStep> steps = approvalStepRepository.findByApprover(user);
            for (ApprovalStep step : steps) {
                if (step.getLeaveRequest() != null) {
                    requests.add(step.getLeaveRequest());
                }
            }
            return new ArrayList<>(requests);
        } else {
            return leaveRequestRepository.findByUser(user);
        }
    }

    public void recordAction(
            User user,
            LeaveRequest leaveRequest,
            String action,
            String previousStatus,
            String newStatus) {

        AuditLog auditLog =
                new AuditLog();

        auditLog.setUser(user);
        auditLog.setLeaveRequest(leaveRequest);
        auditLog.setAction(action);
        auditLog.setPreviousStatus(previousStatus);
        auditLog.setNewStatus(newStatus);
        auditLog.setTimestamp(
                java.time.LocalDateTime.now()
        );

        auditLogRepository.save(auditLog);
    }

    public List<AuditResponseDto> getLeaveRequestAudit(
            LeaveRequest leaveRequest) {

        return auditLogRepository
                .findByLeaveRequestOrderByTimestampAsc(
                        leaveRequest
                )
                .stream()
                .map(this::toDto)
                .toList();
    }

    private AuditResponseDto toDto(
            AuditLog auditLog) {

        AuditResponseDto dto =
                new AuditResponseDto();

        dto.setId(auditLog.getId());

        dto.setLeaveRequestId(
                auditLog
                        .getLeaveRequest()
                        .getId()
        );

        dto.setUserId(
                auditLog
                        .getUser()
                        .getId()
        );

        dto.setUserName(
                auditLog
                        .getUser()
                        .getName()
        );

        dto.setAction(
                auditLog.getAction()
        );

        dto.setPreviousStatus(
                auditLog.getPreviousStatus()
        );

        dto.setNewStatus(
                auditLog.getNewStatus()
        );

        dto.setTimestamp(
                auditLog.getTimestamp()
        );

        return dto;
    }
}