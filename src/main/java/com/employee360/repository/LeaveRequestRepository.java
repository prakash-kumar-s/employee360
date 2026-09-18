package com.employee360.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee360.entity.LeaveRequest;
import com.employee360.entity.User;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByUser(User user);

    List<LeaveRequest> findByUserAndStatusIn(
            User user,
            List<String> statuses
    );

    List<LeaveRequest> findByUserAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            User user,
            LocalDate endDate,
            LocalDate startDate
    );
}