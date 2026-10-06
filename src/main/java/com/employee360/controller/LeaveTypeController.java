package com.employee360.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.employee360.dto.LeaveTypeRequestDto;
import com.employee360.entity.LeaveType;
import com.employee360.service.LeaveTypeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/leave-types")
@PreAuthorize("hasRole('ADMIN')")
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;

    public LeaveTypeController(
            LeaveTypeService leaveTypeService) {

        this.leaveTypeService =
                leaveTypeService;
    }

    @GetMapping
    public ResponseEntity<List<LeaveType>> getAllLeaveTypes() {

        return ResponseEntity.ok(
                leaveTypeService.getAllLeaveTypes()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveType> getLeaveType(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                leaveTypeService.getLeaveType(id)
        );
    }

    @PostMapping
    public ResponseEntity<LeaveType> createLeaveType(
            @Valid
            @RequestBody LeaveTypeRequestDto request) {

        return ResponseEntity.ok(
                leaveTypeService.createLeaveType(
                        request.getName(),
                        request.getEntitlement(),
                        request.getMaxConsecutiveLeave()
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveType> updateLeaveType(
            @PathVariable Long id,
            @Valid
            @RequestBody LeaveTypeRequestDto request) {

        return ResponseEntity.ok(
                leaveTypeService.updateLeaveType(
                        id,
                        request.getName(),
                        request.getEntitlement(),
                        request.getMaxConsecutiveLeave()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveType(
            @PathVariable Long id) {

        leaveTypeService.deleteLeaveType(id);

        return ResponseEntity.noContent().build();
    }
}