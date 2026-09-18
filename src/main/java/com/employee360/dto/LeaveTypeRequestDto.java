package com.employee360.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LeaveTypeRequestDto {

    @NotBlank(message = "Leave type name is required.")
    private String name;

    @NotNull(message = "Entitlement is required.")
    @Min(value = 0, message = "Entitlement cannot be negative.")
    private Integer entitlement;

    @Min(
            value = 1,
            message = "Maximum consecutive leave must be at least 1."
    )
    private Integer maxConsecutiveLeave;

    public String getName() {
        return name;
    }

    public Integer getEntitlement() {
        return entitlement;
    }

    public Integer getMaxConsecutiveLeave() {
        return maxConsecutiveLeave;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEntitlement(Integer entitlement) {
        this.entitlement = entitlement;
    }

    public void setMaxConsecutiveLeave(
            Integer maxConsecutiveLeave) {

        this.maxConsecutiveLeave =
                maxConsecutiveLeave;
    }
}