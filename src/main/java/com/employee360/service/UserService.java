package com.employee360.service;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employee360.dto.UserRequestDto;
import com.employee360.dto.UserResponseDto;
import com.employee360.entity.Department;
import com.employee360.entity.User;
import com.employee360.repository.DepartmentRepository;
import com.employee360.repository.UserRepository;

@Service
public class UserService {

    private static final Set<String> VALID_ROLES = Set.of(
            "ADMIN",
            "HR",
            "MANAGER",
            "DEPARTMENT_HEAD",
            "EMPLOYEE");

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final LeaveBalanceService leaveBalanceService;

    public UserService(
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            PasswordEncoder passwordEncoder,
            LeaveBalanceService leaveBalanceService) {

        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.leaveBalanceService = leaveBalanceService;
    }

    public List<UserResponseDto> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public UserResponseDto createUser(UserRequestDto dto) {

        String username = normalizeUsername(dto.getUsername());
        String role = normalizeRole(dto.getRole());

        if (userRepository.findByUsername(username) != null) {
            throw new RuntimeException("Username already exists.");
        }

        Department department = departmentRepository
                .findById(dto.getDepartmentId())
                .orElseThrow(() ->
                        new RuntimeException("Department not found."));

        User manager = null;

        if (dto.getManagerId() != null) {
            manager = userRepository.findById(dto.getManagerId())
                    .orElseThrow(() ->
                            new RuntimeException("Manager not found."));
        }

        User user = new User();

        user.setName(dto.getName().trim());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(role);
        user.setDepartment(department);
        user.setManager(manager);

        User savedUser = userRepository.save(user);
        leaveBalanceService.initializeForUser(savedUser);

        return toResponse(savedUser);
    }

    public UserResponseDto getUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found."));

        return toResponse(user);
    }

    public UserResponseDto updateUser(
            Long id,
            UserRequestDto dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found."));

        Department department = departmentRepository
                .findById(dto.getDepartmentId())
                .orElseThrow(() ->
                        new RuntimeException("Department not found."));

        String username = normalizeUsername(dto.getUsername());
        String role = normalizeRole(dto.getRole());

        if (!user.getUsername().equals(username)
                && userRepository.findByUsername(username) != null) {
            throw new RuntimeException("Username already exists.");
        }

        user.setName(dto.getName().trim());
        user.setUsername(username);
        user.setRole(role);
        user.setDepartment(department);

        if (dto.getPassword() != null
                && !dto.getPassword().isBlank()) {

            user.setPassword(
                    passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getManagerId() != null) {

            User manager = userRepository.findById(dto.getManagerId())
                    .orElseThrow(() ->
                            new RuntimeException("Manager not found."));

            user.setManager(manager);

        } else {

            user.setManager(null);
        }

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found."));

        userRepository.delete(user);
    }

    private String normalizeUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("Username is required.");
        }

        return username.trim();
    }

    private String normalizeRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new RuntimeException("Role is required.");
        }

        String normalized = role
                .trim()
                .replace('-', '_')
                .replace(' ', '_')
                .toUpperCase(Locale.ROOT);

        if (!VALID_ROLES.contains(normalized)) {
            throw new RuntimeException(
                    "Unsupported role. Allowed roles: ADMIN, HR, MANAGER, DEPARTMENT_HEAD, EMPLOYEE.");
        }

        return normalized;
    }

    private UserResponseDto toResponse(User user) {

        Long departmentId = null;
        String departmentName = null;

        if (user.getDepartment() != null) {
            departmentId = user.getDepartment().getId();
            departmentName = user.getDepartment().getName();
        }

        Long managerId = null;
        String managerName = null;

        if (user.getManager() != null) {
            managerId = user.getManager().getId();
            managerName = user.getManager().getName();
        }

        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getRole(),
                departmentId,
                departmentName,
                managerId,
                managerName);
    }
}