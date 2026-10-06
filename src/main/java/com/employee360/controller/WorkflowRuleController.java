package com.employee360.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.employee360.dto.WorkflowRuleRequestDto;
import com.employee360.entity.WorkflowRule;
import com.employee360.service.WorkflowRuleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/workflow-rules")
@PreAuthorize("hasRole('ADMIN')")
public class WorkflowRuleController {

    private final WorkflowRuleService workflowRuleService;

    public WorkflowRuleController(
            WorkflowRuleService workflowRuleService) {

        this.workflowRuleService =
                workflowRuleService;
    }

    @GetMapping
    public ResponseEntity<List<WorkflowRule>> getAllRules() {

        return ResponseEntity.ok(
                workflowRuleService.getAllRules()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkflowRule> getRule(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                workflowRuleService.getRule(id)
        );
    }

    @PostMapping
    public ResponseEntity<WorkflowRule> createRule(
            @Valid
            @RequestBody WorkflowRuleRequestDto request) {

        return ResponseEntity.ok(
                workflowRuleService.createRule(
                        request.getMinDays(),
                        request.getMaxDays(),
                        request.getApproverRole(),
                        request.getApprovalLevel()
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkflowRule> updateRule(
            @PathVariable Long id,
            @Valid
            @RequestBody WorkflowRuleRequestDto request) {

        return ResponseEntity.ok(
                workflowRuleService.updateRule(
                        id,
                        request.getMinDays(),
                        request.getMaxDays(),
                        request.getApproverRole(),
                        request.getApprovalLevel()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(
            @PathVariable Long id) {

        workflowRuleService.deleteRule(id);

        return ResponseEntity.noContent().build();
    }
}