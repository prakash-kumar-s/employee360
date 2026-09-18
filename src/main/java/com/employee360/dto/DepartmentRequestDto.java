package com.employee360.dto;

import jakarta.validation.constraints.NotBlank;

public class DepartmentRequestDto {

    @NotBlank(message = "Department name is required.")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}