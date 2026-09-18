package com.employee360.dto;

public class LeaveTypeResponseDto {

    private Long id;
    private String name;
    private int entitlement;
    private Integer maxConsecutiveLeave;

    public LeaveTypeResponseDto() {
    }

    public LeaveTypeResponseDto(
            Long id,
            String name,
            int entitlement,
            Integer maxConsecutiveLeave) {

        this.id = id;
        this.name = name;
        this.entitlement = entitlement;
        this.maxConsecutiveLeave = maxConsecutiveLeave;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getEntitlement() {
        return entitlement;
    }

    public Integer getMaxConsecutiveLeave() {
        return maxConsecutiveLeave;
    }
}