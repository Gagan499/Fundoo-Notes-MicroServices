package com.example.NotesService.service;

import com.example.NotesService.dto.ReminderRequestDTO;
import com.example.NotesService.dto.ReminderResponseDTO;
import java.util.List;

public interface ReminderService {
    ReminderResponseDTO createReminder(ReminderRequestDTO request, Long userId);

    List<ReminderResponseDTO> getRemindersByUserId(Long userId);

    ReminderResponseDTO getReminderById(Long reminderId, Long userId);

    ReminderResponseDTO updateReminder(ReminderRequestDTO request, Long reminderId, Long userId);

    void deleteReminder(Long reminderId, Long userId);

    void publishDueReminders();
}
