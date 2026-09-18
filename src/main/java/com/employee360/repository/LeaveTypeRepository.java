package com.employee360.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee360.entity.LeaveType;

public interface LeaveTypeRepository
        extends JpaRepository<LeaveType, Long> {

    boolean existsByNameIgnoreCase(String name);
}