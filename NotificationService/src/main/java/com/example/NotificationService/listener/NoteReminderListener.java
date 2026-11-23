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
            containerFactory = "reminderKafkaListenerContainerFactory"
    )
    public void consume(NoteReminderEvent event) {

        // Step 1: Confirm Kafka received the message
        log.info("========== KAFKA MESSAGE RECEIVED ==========");
        log.info("Reminder ID: {}", event.reminderId());
        log.info("Note ID: {}", event.noteId());
        log.info("Recipient: {}", event.recipientEmail());
        log.info("Note Title: {}", event.noteTitle());

        try {
            // Step 2: Send notification
            NotificationResponseDTO response =
                    notificationService.sendReminderNotification(event);

            // Step 3: Confirm successful processing
            log.info("Notification processed successfully. Reminder ID: {}",
                    response.reminderId());

        } catch (Exception exception) {

            log.error("Failed to process reminder ID: {}",
                    event.reminderId(), exception);

            throw exception;
        }
    }
}
