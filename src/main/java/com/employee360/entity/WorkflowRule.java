package com.employee360.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class WorkflowRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer minDays;

    private Integer maxDays;

    private String approverRole;

    private int approvalLevel;

    public Long getId() {
        return id;
    }

    public Integer getMinDays() {
        return minDays;
    }

    public Integer getMaxDays() {
        return maxDays;
    }

    public String getApproverRole() {
        return approverRole;
    }

    public int getApprovalLevel() {
        return approvalLevel;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMinDays(Integer minDays) {
        this.minDays = minDays;
    }

    public void setMaxDays(Integer maxDays) {
        this.maxDays = maxDays;
    }

    public void setApproverRole(String approverRole) {
        this.approverRole = approverRole;
    }

    public void setApprovalLevel(int approvalLevel) {
        this.approvalLevel = approvalLevel;
    }
}