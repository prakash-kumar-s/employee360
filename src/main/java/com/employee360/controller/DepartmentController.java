package com.employee360.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.employee360.dto.DepartmentRequestDto;
import com.employee360.entity.Department;
import com.employee360.service.DepartmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/departments")
@PreAuthorize("hasRole('ADMIN')")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(
            DepartmentService departmentService) {

        this.departmentService =
                departmentService;
    }

    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments() {

        return ResponseEntity.ok(
                departmentService.getAllDepartments()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                departmentService.getDepartment(id)
        );
    }

    @PostMapping
    public ResponseEntity<Department> createDepartment(
            @Valid
            @RequestBody DepartmentRequestDto request) {

        return ResponseEntity.ok(
                departmentService.createDepartment(
                        request.getName()
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(
            @PathVariable Long id,
            @Valid
            @RequestBody DepartmentRequestDto request) {

        return ResponseEntity.ok(
                departmentService.updateDepartment(
                        id,
                        request.getName()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(
            @PathVariable Long id) {

        departmentService.deleteDepartment(id);

        return ResponseEntity.noContent().build();
    }
}