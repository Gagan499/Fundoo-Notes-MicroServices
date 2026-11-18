package com.example.NotificationService.service;

import com.example.NotificationService.dto.NotificationResponseDTO;
import com.example.NotificationService.event.NoteReminderEvent;

public interface NotificationService {
    NotificationResponseDTO sendReminderNotification(NoteReminderEvent event);
}
