package com.employee360.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee360.entity.AuditLog;
import com.employee360.entity.LeaveRequest;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByLeaveRequestOrderByTimestampAsc(
            LeaveRequest leaveRequest
    );
}