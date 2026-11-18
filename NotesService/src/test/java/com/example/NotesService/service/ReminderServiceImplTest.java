package com.example.NotesService.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.NotesService.dto.ReminderRequestDTO;
import com.example.NotesService.entity.Note;
import com.example.NotesService.entity.NoteReminder;
import com.example.NotesService.event.NoteReminderEvent;
import com.example.NotesService.repository.NoteReminderRepository;
import com.example.NotesService.repository.NoteRepository;
import com.example.NotesService.service.impl.ReminderServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

@ExtendWith(MockitoExtension.class)
class ReminderServiceImplTest {

    @Mock
    private NoteReminderRepository reminderRepository;

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private KafkaTemplate<String, NoteReminderEvent> kafkaTemplate;

    private ReminderServiceImpl reminderService;

    @BeforeEach
    void setUp() {
        reminderService = new ReminderServiceImpl(
                reminderRepository,
                noteRepository,
                kafkaTemplate,
                "note-reminder");
    }

    @Test
    void createsReminderOnlyForTheAuthenticatedUsersNote() {
        Note note = Note.builder().noteId(10L).userId(42L).title("Call").build();
        ReminderRequestDTO request = new ReminderRequestDTO();
        request.setNoteId(10L);
        request.setReminderTime(LocalDateTime.now().plusHours(1));
        when(noteRepository.findByNoteIdAndUserId(10L, 42L)).thenReturn(Optional.of(note));
        when(reminderRepository.save(any(NoteReminder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = reminderService.createReminder(request, 42L, "user@example.com");

        assertEquals(10L, result.getNoteId());
        verify(reminderRepository).save(any(NoteReminder.class));
    }

    @Test
    void marksReminderNotifiedOnlyAfterPublishingToKafka() {
        Note note = Note.builder().noteId(10L).title("Call").build();
        NoteReminder reminder = NoteReminder.builder()
                .reminderId(7L)
                .note(note)
                .userId(42L)
                .userEmail("user@example.com")
                .reminderTime(LocalDateTime.now().minusMinutes(1))
                .build();
        when(reminderRepository.findByNotifiedFalseAndReminderTimeLessThanEqual(any(LocalDateTime.class)))
                .thenReturn(List.of(reminder));
        when(kafkaTemplate.send(eq("note-reminder"), anyString(), any(NoteReminderEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        reminderService.publishDueReminders();

        assertTrue(reminder.isNotified());
        verify(reminderRepository).save(reminder);
    }

    @Test
    void reminderEventCanBeSerializedForKafka() {
        NoteReminderEvent event = new NoteReminderEvent(
                7L, 10L, 42L, "user@example.com", "Call", LocalDateTime.now());
        JacksonJsonSerializer<NoteReminderEvent> serializer = new JacksonJsonSerializer<>();

        assertTrue(serializer.serialize("note-reminder", event).length > 0);
    }
}
