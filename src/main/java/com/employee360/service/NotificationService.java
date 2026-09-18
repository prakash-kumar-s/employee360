package com.employee360.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.employee360.dto.NotificationResponseDto;
import com.employee360.entity.LeaveRequest;
import com.employee360.entity.Notification;
import com.employee360.entity.User;
import com.employee360.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository) {

        this.notificationRepository =
                notificationRepository;
    }

    public void createNotification(
            User user,
            LeaveRequest leaveRequest,
            String type,
            String message) {

        Notification notification =
                new Notification();

        notification.setUser(user);
        notification.setLeaveRequest(leaveRequest);
        notification.setType(type);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(
                java.time.LocalDateTime.now()
        );

        notificationRepository.save(notification);
    }

    public List<NotificationResponseDto> getUserNotifications(
            User user) {

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private NotificationResponseDto toDto(
            Notification notification) {

        NotificationResponseDto dto =
                new NotificationResponseDto();

        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(
                notification.getCreatedAt()
        );
        dto.setUserId(
                notification.getUser().getId()
        );

        if (notification.getLeaveRequest() != null) {
            dto.setLeaveRequestId(
                    notification
                            .getLeaveRequest()
                            .getId()
            );
        }

        return dto;
    }
}