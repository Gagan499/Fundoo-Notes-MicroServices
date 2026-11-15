package com.example.NotesService.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReminderResponseDTO {
    private Long reminderId;
    private Long noteId;
    private LocalDateTime reminderTime;
    private boolean notified;
    private LocalDateTime createdAt;
}
