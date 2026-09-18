package com.employee360.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee360.entity.WorkflowRule;

public interface WorkflowRuleRepository
        extends JpaRepository<WorkflowRule, Long> {

    List<WorkflowRule> findAllByOrderByApprovalLevelAsc();
}