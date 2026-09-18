package com.employee360.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee360.entity.Department;
import com.employee360.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    List<User> findByRole(String role);

    List<User> findByDepartmentAndRole(
            Department department,
            String role);

    List<User> findByManager(User manager);
}