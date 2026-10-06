package com.employee360.config;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.employee360.entity.Department;
import com.employee360.entity.LeaveType;
import com.employee360.entity.User;
import com.employee360.entity.WorkflowRule;
import com.employee360.repository.DepartmentRepository;
import com.employee360.repository.LeaveTypeRepository;
import com.employee360.repository.UserRepository;
import com.employee360.repository.WorkflowRuleRepository;
import com.employee360.service.LeaveBalanceService;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final WorkflowRuleRepository workflowRuleRepository;
    private final PasswordEncoder passwordEncoder;
    private final LeaveBalanceService leaveBalanceService;

    public DataInitializer(
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            LeaveTypeRepository leaveTypeRepository,
            WorkflowRuleRepository workflowRuleRepository,
            PasswordEncoder passwordEncoder,
            LeaveBalanceService leaveBalanceService) {

        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.workflowRuleRepository = workflowRuleRepository;
        this.passwordEncoder = passwordEncoder;
        this.leaveBalanceService = leaveBalanceService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() == 0) {
            seedUsers();
        }

        createLeaveType("Annual Leave", 20, 10);
        createLeaveType("Sick Leave", 12, 5);
        createLeaveType("Casual Leave", 10, 3);

        migrateDefaultWorkflowRule("MANAGER", 1, 3, 1, null, 1);
        migrateDefaultWorkflowRule("DEPARTMENT_HEAD", 4, null, 6, null, 2);
        createWorkflowRule(1, null, "MANAGER", 1);
        createWorkflowRule(6, null, "DEPARTMENT_HEAD", 2);
        createWorkflowRule(8, null, "HR", 3);

        leaveBalanceService.initializeMissingBalances();
    }

    private void seedUsers() {
        Department engineering = createDepartment("Engineering");
        Department hrDepartment = createDepartment("Human Resources");

        User admin = createUser(
                "System Admin",
                "admin",
                "admin123",
                "ADMIN",
                engineering,
                null);

        User hrManager = createUser(
                "HR Manager",
                "hr",
                "hr123",
                "HR",
                hrDepartment,
                admin);

        User departmentHead = createUser(
                "Engineering Head",
                "depthead",
                "depthead123",
                "DEPARTMENT_HEAD",
                engineering,
                admin);

        User manager = createUser(
                "Project Manager",
                "manager",
                "manager123",
                "MANAGER",
                engineering,
                departmentHead);

        User employee = createUser(
                "Jane Employee",
                "employee",
                "employee123",
                "EMPLOYEE",
                engineering,
                manager);

        userRepository.saveAll(List.of(admin, hrManager, departmentHead, manager, employee));
    }

    private Department createDepartment(String name) {
        Department department = new Department();
        department.setName(name);
        return departmentRepository.save(department);
    }

    private User createUser(
            String name,
            String username,
            String password,
            String role,
            Department department,
            User manager) {

        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setDepartment(department);
        user.setManager(manager);
        return user;
    }

    private void createLeaveType(String name, int entitlement, int maxConsecutiveLeave) {
        if (leaveTypeRepository.findAll().stream().anyMatch(type -> type.getName().equalsIgnoreCase(name))) {
            return;
        }

        LeaveType leaveType = new LeaveType();
        leaveType.setName(name);
        leaveType.setEntitlement(entitlement);
        leaveType.setMaxConsecutiveLeave(maxConsecutiveLeave);
        leaveTypeRepository.save(leaveType);
    }

    private void createWorkflowRule(Integer minDays, Integer maxDays, String approverRole, int approvalLevel) {
        if (workflowRuleRepository.findAll().stream().anyMatch(rule ->
                rule.getApproverRole() != null && rule.getApproverRole().equalsIgnoreCase(approverRole)
                        && java.util.Objects.equals(rule.getMinDays(), minDays)
                        && java.util.Objects.equals(rule.getMaxDays(), maxDays))) {
            return;
        }

        WorkflowRule rule = new WorkflowRule();
        rule.setMinDays(minDays);
        rule.setMaxDays(maxDays);
        rule.setApproverRole(approverRole);
        rule.setApprovalLevel(approvalLevel);
        workflowRuleRepository.save(rule);
    }

    private void migrateDefaultWorkflowRule(
            String approverRole,
            Integer oldMinDays,
            Integer oldMaxDays,
            Integer newMinDays,
            Integer newMaxDays,
            int approvalLevel) {

        workflowRuleRepository.findAll().stream()
                .filter(rule -> approverRole.equalsIgnoreCase(rule.getApproverRole()))
                .filter(rule -> java.util.Objects.equals(rule.getMinDays(), oldMinDays))
                .filter(rule -> java.util.Objects.equals(rule.getMaxDays(), oldMaxDays))
                .findFirst()
                .ifPresent(rule -> {
                    rule.setMinDays(newMinDays);
                    rule.setMaxDays(newMaxDays);
                    rule.setApprovalLevel(approvalLevel);
                    workflowRuleRepository.save(rule);
                });
    }
}
