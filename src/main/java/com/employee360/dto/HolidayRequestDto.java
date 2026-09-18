package com.employee360.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class HolidayRequestDto {

    @NotBlank(message = "Holiday name is required.")
    private String name;

    @NotNull(message = "Holiday date is required.")
    private LocalDate date;

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}