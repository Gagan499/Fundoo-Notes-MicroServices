package com.example.NotificationService.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import com.example.NotificationService.event.NoteReminderEvent;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl(mailSender, "no-reply@fundoo.local");
    }

    @Test
    void sendsReminderEmailToTheEventRecipient() {
        NoteReminderEvent event = new NoteReminderEvent(
                7L, 10L, 42L, "user@example.com", "Call", LocalDateTime.now());

        var result = notificationService.sendReminderNotification(event);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        assertEquals("user@example.com", messageCaptor.getValue().getTo()[0]);
        assertEquals("Reminder: Call", messageCaptor.getValue().getSubject());
        assertEquals(7L, result.reminderId());
        assertEquals("sent", result.status());
    }

    @Test
    void rejectsReminderEventsWithoutAnEmailAddress() {
        NoteReminderEvent event = new NoteReminderEvent(
                7L, 10L, 42L, " ", "Call", LocalDateTime.now());

        assertThrows(IllegalArgumentException.class, () -> notificationService.sendReminderNotification(event));
    }

    @Test
    void consumerDeserializerReadsEventsFromTheNotesServiceProducer() {
        NoteReminderEvent event = new NoteReminderEvent(
                7L, 10L, 42L, "user@example.com", "Call", LocalDateTime.now());
        byte[] payload = new JacksonJsonSerializer<NoteReminderEvent>()
                .noTypeInfo()
                .serialize("note-reminder", event);
        JacksonJsonDeserializer<NoteReminderEvent> deserializer =
                new JacksonJsonDeserializer<>(NoteReminderEvent.class).ignoreTypeHeaders();

        NoteReminderEvent result = deserializer.deserialize("note-reminder", payload);

        assertEquals(event.recipientEmail(), result.recipientEmail());
        assertEquals(event.reminderTime(), result.reminderTime());
    }
}
