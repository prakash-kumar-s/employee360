package com.employee360.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee360.entity.Department;

public interface DepartmentRepository
        extends JpaRepository<Department, Long> {

    boolean existsByNameIgnoreCase(String name);
}