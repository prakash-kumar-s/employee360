package com.employee360.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int entitlement;

    private Integer maxConsecutiveLeave;

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

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEntitlement(int entitlement) {
        this.entitlement = entitlement;
    }

    public void setMaxConsecutiveLeave(
            Integer maxConsecutiveLeave) {

        this.maxConsecutiveLeave =
                maxConsecutiveLeave;
    }
}