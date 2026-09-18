package com.employee360.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.employee360.dto.NotificationResponseDto;
import com.employee360.entity.User;
import com.employee360.repository.UserRepository;
import com.employee360.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(
            NotificationService notificationService,
            UserRepository userRepository) {

        this.notificationService =
                notificationService;
        this.userRepository =
                userRepository;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDto>> getNotifications(
            @PathVariable Long userId,
            Authentication authentication) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        if (!authenticatedUser.getId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                notificationService.getUserNotifications(
                        authenticatedUser
                )
        );
    }

    private User getAuthenticatedUser(
            Authentication authentication) {

        String username =
                authentication.getName();

        User user =
                userRepository.findByUsername(username);

        if (user == null) {
            throw new IllegalArgumentException(
                    "Authenticated user not found."
            );
        }

        return user;
    }
}