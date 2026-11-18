package com.example.NotificationService.listener;

import com.example.NotificationService.dto.NotificationResponseDTO;
import com.example.NotificationService.event.NoteReminderEvent;
import com.example.NotificationService.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NoteReminderListener {

    private final NotificationService notificationService;

    public NoteReminderListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "${notification.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "reminderKafkaListenerContainerFactory")
    public void consume(NoteReminderEvent event) {
        NotificationResponseDTO response = notificationService.sendReminderNotification(event);
        log.info("Processed reminder notification {}", response.reminderId());
    }
}
