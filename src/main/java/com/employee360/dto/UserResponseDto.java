package com.employee360.dto;

public class UserResponseDto {

    private Long id;
    private String name;
    private String username;
    private String role;

    private Long departmentId;
    private String departmentName;

    private Long managerId;
    private String managerName;

    public UserResponseDto() {
    }

    public UserResponseDto(
            Long id,
            String name,
            String username,
            String role,
            Long departmentId,
            String departmentName,
            Long managerId,
            String managerName) {

        this.id = id;
        this.name = name;
        this.username = username;
        this.role = role;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.managerId = managerId;
        this.managerName = managerName;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public Long getManagerId() {
        return managerId;
    }

    public String getManagerName() {
        return managerName;
    }
}