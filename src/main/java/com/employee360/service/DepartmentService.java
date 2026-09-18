package com.employee360.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.employee360.entity.Department;
import com.employee360.repository.DepartmentRepository;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(
            DepartmentRepository departmentRepository) {

        this.departmentRepository =
                departmentRepository;
    }

    public List<Department> getAllDepartments() {

        return departmentRepository.findAll();
    }

    public Department getDepartment(Long id) {

        return departmentRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Department not found."
                        )
                );
    }

    public Department createDepartment(
            String name) {

        if (departmentRepository
                .existsByNameIgnoreCase(name)) {

            throw new IllegalArgumentException(
                    "Department already exists."
            );
        }

        Department department =
                new Department();

        department.setName(name.trim());

        return departmentRepository.save(
                department
        );
    }

    public Department updateDepartment(
            Long id,
            String name) {

        Department department =
                getDepartment(id);

        if (!department.getName()
                .equalsIgnoreCase(name.trim())
                && departmentRepository
                        .existsByNameIgnoreCase(
                                name.trim()
                        )) {

            throw new IllegalArgumentException(
                    "Department already exists."
            );
        }

        department.setName(name.trim());

        return departmentRepository.save(
                department
        );
    }

    public void deleteDepartment(Long id) {

        Department department =
                getDepartment(id);

        departmentRepository.delete(department);
    }
}