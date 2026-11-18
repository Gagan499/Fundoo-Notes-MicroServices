package com.example.NotesService.event;

import java.time.LocalDateTime;

public record NoteReminderEvent(
        Long reminderId,
        Long noteId,
        Long userId,
        String recipientEmail,
        String noteTitle,
        LocalDateTime reminderTime) {
}
