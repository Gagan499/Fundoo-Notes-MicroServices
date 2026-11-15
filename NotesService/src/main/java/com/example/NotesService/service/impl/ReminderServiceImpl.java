package com.example.NotesService.service.impl;

import com.example.NotesService.dto.ReminderRequestDTO;
import com.example.NotesService.dto.ReminderResponseDTO;
import com.example.NotesService.entity.Note;
import com.example.NotesService.entity.NoteReminder;
import com.example.NotesService.event.NoteReminderEvent;
import com.example.NotesService.exception.NoteNotFoundByIdException;
import com.example.NotesService.exception.ReminderNotFoundException;
import com.example.NotesService.repository.NoteReminderRepository;
import com.example.NotesService.repository.NoteRepository;
import com.example.NotesService.service.ReminderService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ReminderServiceImpl implements ReminderService {

    private final NoteReminderRepository reminderRepository;
    private final NoteRepository noteRepository;
    private final KafkaTemplate<String, NoteReminderEvent> kafkaTemplate;
    private final String reminderTopic;

    public ReminderServiceImpl(
            NoteReminderRepository reminderRepository,
            NoteRepository noteRepository,
            KafkaTemplate<String, NoteReminderEvent> kafkaTemplate,
            @Value("${notes.reminders.topic}") String reminderTopic) {
        this.reminderRepository = reminderRepository;
        this.noteRepository = noteRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.reminderTopic = reminderTopic;
    }

    @Override
    @Transactional
    public ReminderResponseDTO createReminder(ReminderRequestDTO request, Long userId) {
        Note note = findOwnedNote(request.getNoteId(), userId);
        NoteReminder reminder = NoteReminder.builder()
                .note(note)
                .userId(userId)
                .reminderTime(request.getReminderTime())
                .build();
        return toResponse(reminderRepository.save(reminder));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReminderResponseDTO> getRemindersByUserId(Long userId) {
        return reminderRepository.findByUserIdOrderByReminderTimeAsc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReminderResponseDTO getReminderById(Long reminderId, Long userId) {
        return toResponse(findOwnedReminder(reminderId, userId));
    }

    @Override
    @Transactional
    public ReminderResponseDTO updateReminder(ReminderRequestDTO request, Long reminderId, Long userId) {
        NoteReminder reminder = findOwnedReminder(reminderId, userId);
        Note note = findOwnedNote(request.getNoteId(), userId);
        reminder.setNote(note);
        reminder.setReminderTime(request.getReminderTime());
        reminder.setNotified(false);
        return toResponse(reminderRepository.save(reminder));
    }

    @Override
    @Transactional
    public void deleteReminder(Long reminderId, Long userId) {
        reminderRepository.delete(findOwnedReminder(reminderId, userId));
    }

    @Override
    @Transactional
    public void publishDueReminders() {
        List<NoteReminder> dueReminders = reminderRepository
                .findByNotifiedFalseAndReminderTimeLessThanEqual(LocalDateTime.now());
        for (NoteReminder reminder : dueReminders) {
            Note note = reminder.getNote();
            NoteReminderEvent event = new NoteReminderEvent(
                    reminder.getReminderId(),
                    note.getNoteId(),
                    reminder.getUserId(),
                    note.getTitle(),
                    reminder.getReminderTime());
            try {
                kafkaTemplate.send(reminderTopic, reminder.getReminderId().toString(), event).get();
                reminder.setNotified(true);
                reminderRepository.save(reminder);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while publishing a note reminder", exception);
            } catch (ExecutionException exception) {
                log.error("Could not publish reminder {}", reminder.getReminderId(), exception.getCause());
            }
        }
    }

    private Note findOwnedNote(Long noteId, Long userId) {
        return noteRepository.findByNoteIdAndUserId(noteId, userId)
                .orElseThrow(() -> new NoteNotFoundByIdException("Note not found"));
    }

    private NoteReminder findOwnedReminder(Long reminderId, Long userId) {
        return reminderRepository.findByReminderIdAndUserId(reminderId, userId)
                .orElseThrow(() -> new ReminderNotFoundException("Reminder not found"));
    }

    private ReminderResponseDTO toResponse(NoteReminder reminder) {
        return new ReminderResponseDTO(
                reminder.getReminderId(),
                reminder.getNote().getNoteId(),
                reminder.getReminderTime(),
                reminder.isNotified(),
                reminder.getCreatedAt());
    }
}
