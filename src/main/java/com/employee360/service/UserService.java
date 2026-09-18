package com.employee360.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.employee360.dto.UserRequestDto;
import com.employee360.dto.UserResponseDto;
import com.employee360.entity.Department;
import com.employee360.entity.User;
import com.employee360.repository.DepartmentRepository;
import com.employee360.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponseDto> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponseDto createUser(UserRequestDto dto) {

        if (userRepository.findByUsername(dto.getUsername()) != null) {
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

        user.setName(dto.getName());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        user.setDepartment(department);
        user.setManager(manager);

        User savedUser = userRepository.save(user);

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

        user.setName(dto.getName());
        user.setUsername(dto.getUsername());
        user.setRole(dto.getRole());
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