package com.example.NotificationService.dto;

import java.time.LocalDateTime;

public record NotificationResponseDTO(
        Long reminderId,
        String recipientEmail,
        String status,
        LocalDateTime sentAt) {
}
