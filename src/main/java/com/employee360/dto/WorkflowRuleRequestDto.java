package com.employee360.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class WorkflowRuleRequestDto {

    @NotNull(message = "Minimum days is required.")
    @Min(value = 1, message = "Minimum days must be at least 1.")
    private Integer minDays;

    private Integer maxDays;

    @NotBlank(message = "Approver role is required.")
    private String approverRole;

    @NotNull(message = "Approval level is required.")
    @Min(value = 1, message = "Approval level must be at least 1.")
    private Integer approvalLevel;

    public Integer getMinDays() {
        return minDays;
    }

    public Integer getMaxDays() {
        return maxDays;
    }

    public String getApproverRole() {
        return approverRole;
    }

    public Integer getApprovalLevel() {
        return approvalLevel;
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

    public void setApprovalLevel(Integer approvalLevel) {
        this.approvalLevel = approvalLevel;
    }
}