package com.employee360.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee360.entity.LeaveBalance;
import com.employee360.entity.LeaveType;
import com.employee360.entity.User;

public interface LeaveBalanceRepository
        extends JpaRepository<LeaveBalance, Long> {

    LeaveBalance findByUserAndLeaveType(
            User user,
            LeaveType leaveType
    );

    List<LeaveBalance> findByUser(User user);
}