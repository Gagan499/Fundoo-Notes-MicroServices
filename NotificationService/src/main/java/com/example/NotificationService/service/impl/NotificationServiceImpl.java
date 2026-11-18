package com.example.NotificationService.service.impl;

import com.example.NotificationService.dto.NotificationResponseDTO;
import com.example.NotificationService.event.NoteReminderEvent;
import com.example.NotificationService.service.NotificationService;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;
    private final String senderAddress;

    public NotificationServiceImpl(
            JavaMailSender mailSender,
            @Value("${notification.mail.from}") String senderAddress) {
        this.mailSender = mailSender;
        this.senderAddress = senderAddress;
    }

    @Override
    public NotificationResponseDTO sendReminderNotification(NoteReminderEvent event) {
        if (!StringUtils.hasText(event.recipientEmail())) {
            throw new IllegalArgumentException("Reminder event has no recipient email");
        }

        String noteTitle = StringUtils.hasText(event.noteTitle()) ? event.noteTitle() : "your note";
        String subject = "Reminder: " + noteTitle;
        String body = "This is your reminder for \"" + noteTitle + "\". It was scheduled for "
                + event.reminderTime() + ".";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderAddress);
        message.setTo(event.recipientEmail());
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);

        log.info("Sent reminder notification {}", event.reminderId());
        return new NotificationResponseDTO(
                event.reminderId(),
                event.recipientEmail(),
                "sent",
                LocalDateTime.now());
    }
}
