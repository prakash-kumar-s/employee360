package com.employee360.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.employee360.entity.WorkflowRule;
import com.employee360.repository.WorkflowRuleRepository;

@Service
public class WorkflowRuleService {

    private final WorkflowRuleRepository workflowRuleRepository;

    public WorkflowRuleService(
            WorkflowRuleRepository workflowRuleRepository) {

        this.workflowRuleRepository =
                workflowRuleRepository;
    }

    public List<WorkflowRule> getAllRules() {

        return workflowRuleRepository
                .findAllByOrderByApprovalLevelAsc();
    }

    public WorkflowRule getRule(Long id) {

        return workflowRuleRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Workflow rule not found."
                        )
                );
    }

    public WorkflowRule createRule(
            Integer minDays,
            Integer maxDays,
            String approverRole,
            int approvalLevel) {

        validateRule(
                minDays,
                maxDays,
                approverRole,
                approvalLevel
        );

        WorkflowRule rule =
                new WorkflowRule();

        rule.setMinDays(minDays);
        rule.setMaxDays(maxDays);
        rule.setApproverRole(
                approverRole.trim().toUpperCase()
        );
        rule.setApprovalLevel(approvalLevel);

        return workflowRuleRepository.save(rule);
    }

    public WorkflowRule updateRule(
            Long id,
            Integer minDays,
            Integer maxDays,
            String approverRole,
            int approvalLevel) {

        validateRule(
                minDays,
                maxDays,
                approverRole,
                approvalLevel
        );

        WorkflowRule rule =
                getRule(id);

        rule.setMinDays(minDays);
        rule.setMaxDays(maxDays);
        rule.setApproverRole(
                approverRole.trim().toUpperCase()
        );
        rule.setApprovalLevel(approvalLevel);

        return workflowRuleRepository.save(rule);
    }

    public void deleteRule(Long id) {

        WorkflowRule rule =
                getRule(id);

        workflowRuleRepository.delete(rule);
    }

    private void validateRule(
            Integer minDays,
            Integer maxDays,
            String approverRole,
            int approvalLevel) {

        if (minDays == null || minDays < 1) {

            throw new IllegalArgumentException(
                    "Minimum days must be at least 1."
            );
        }

        if (maxDays != null
                && maxDays < minDays) {

            throw new IllegalArgumentException(
                    "Maximum days cannot be less than minimum days."
            );
        }

        if (approverRole == null
                || approverRole.isBlank()) {

            throw new IllegalArgumentException(
                    "Approver role is required."
            );
        }

        if (approvalLevel < 1) {

            throw new IllegalArgumentException(
                    "Approval level must be at least 1."
            );
        }
    }
}