package com.example.NotesService.controller;

import com.example.NotesService.config.NotesPrincipal;
import com.example.NotesService.dto.ApiResponse;
import com.example.NotesService.dto.ReminderRequestDTO;
import com.example.NotesService.dto.ReminderResponseDTO;
import com.example.NotesService.service.ReminderService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReminderResponseDTO>> create(
            @AuthenticationPrincipal NotesPrincipal principal,
            @Valid @RequestBody ReminderRequestDTO request) {
        ReminderResponseDTO reminder =
                reminderService.createReminder(request, principal.userId(), principal.email());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reminder created successfully", reminder));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReminderResponseDTO>>> getAll(
            @AuthenticationPrincipal NotesPrincipal principal) {
        List<ReminderResponseDTO> reminders = reminderService.getRemindersByUserId(principal.userId());
        String message = reminders.isEmpty() ? "No reminders found" : "Reminders retrieved successfully";
        return ResponseEntity.ok(ApiResponse.success(message, reminders));
    }

    @GetMapping("/{reminderId}")
    public ResponseEntity<ApiResponse<ReminderResponseDTO>> getById(
            @AuthenticationPrincipal NotesPrincipal principal,
            @PathVariable Long reminderId) {
        ReminderResponseDTO reminder = reminderService.getReminderById(reminderId, principal.userId());
        return ResponseEntity.ok(ApiResponse.success("Reminder retrieved successfully", reminder));
    }

    @PutMapping("/{reminderId}")
    public ResponseEntity<ApiResponse<ReminderResponseDTO>> update(
            @AuthenticationPrincipal NotesPrincipal principal,
            @PathVariable Long reminderId,
            @Valid @RequestBody ReminderRequestDTO request) {
        ReminderResponseDTO reminder =
                reminderService.updateReminder(request, reminderId, principal.userId(), principal.email());
        return ResponseEntity.ok(ApiResponse.success("Reminder updated successfully", reminder));
    }

    @DeleteMapping("/{reminderId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal NotesPrincipal principal,
            @PathVariable Long reminderId) {
        reminderService.deleteReminder(reminderId, principal.userId());
        return ResponseEntity.ok(ApiResponse.success("Reminder deleted successfully", null));
    }
}
