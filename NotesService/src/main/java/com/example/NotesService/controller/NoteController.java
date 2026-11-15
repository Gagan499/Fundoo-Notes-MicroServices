package com.example.NotesService.controller;

import com.example.NotesService.config.NotesPrincipal;
import com.example.NotesService.dto.ApiResponse;
import com.example.NotesService.dto.NoteRequestDTO;
import com.example.NotesService.dto.NoteResponseDTO;
import com.example.NotesService.service.NoteService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notes")
@Slf4j
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping("/createNote")
    public ResponseEntity<ApiResponse<NoteResponseDTO>> create(
            @AuthenticationPrincipal NotesPrincipal principal,
            @Valid @RequestBody NoteRequestDTO request) {
        NoteResponseDTO note = noteService.createNote(request, principal.userId());
        log.info("Note created");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Note created successfully", note));
    }

    @GetMapping("/get-notes")
    public ResponseEntity<ApiResponse<List<NoteResponseDTO>>> getAll(
            @AuthenticationPrincipal NotesPrincipal principal) {
        List<NoteResponseDTO> notes = noteService.getAllNotesByUserId(principal.userId());
        String message = notes.isEmpty() ? "No notes found" : "Notes retrieved successfully";
        return ResponseEntity.ok(ApiResponse.success(message, notes));
    }

    @GetMapping("/archived-notes")
    public ResponseEntity<ApiResponse<List<NoteResponseDTO>>> getArchived(
            @AuthenticationPrincipal NotesPrincipal principal) {
        List<NoteResponseDTO> notes = noteService.getArchivedNotesByUserId(principal.userId());
        String message = notes.isEmpty() ? "No archived notes found" : "Archived notes retrieved successfully";
        return ResponseEntity.ok(ApiResponse.success(message, notes));
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<ApiResponse<NoteResponseDTO>> getById(
            @AuthenticationPrincipal NotesPrincipal principal,
            @PathVariable Long noteId) {
        NoteResponseDTO note = noteService.getNoteById(noteId, principal.userId());
        return ResponseEntity.ok(ApiResponse.success("Note retrieved successfully", note));
    }

    @PatchMapping("/{noteId}")
    public ResponseEntity<ApiResponse<NoteResponseDTO>> update(
            @AuthenticationPrincipal NotesPrincipal principal,
            @PathVariable Long noteId,
            @RequestBody NoteRequestDTO request) {
        NoteResponseDTO note = noteService.editNote(request, noteId, principal.userId());
        return ResponseEntity.ok(ApiResponse.success("Note updated successfully", note));
    }

    @PatchMapping("/trashing/{noteId}")
    public ResponseEntity<ApiResponse<Void>> toggleTrash(
            @AuthenticationPrincipal NotesPrincipal principal,
            @PathVariable Long noteId) {
        boolean trashed = noteService.toggleTrash(noteId, principal.userId());
        String message = trashed ? "Note trashed successfully" : "Note restored successfully";
        return ResponseEntity.ok(ApiResponse.success(message, null));
    }

    @PatchMapping("/archiving/{noteId}")
    public ResponseEntity<ApiResponse<Void>> toggleArchive(
            @AuthenticationPrincipal NotesPrincipal principal,
            @PathVariable Long noteId) {
        boolean archived = noteService.toggleArchive(noteId, principal.userId());
        String message = archived ? "Note archived successfully" : "Note unarchived successfully";
        return ResponseEntity.ok(ApiResponse.success(message, null));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal NotesPrincipal principal,
            @PathVariable Long noteId) {
        noteService.deleteNoteById(noteId, principal.userId());
        return ResponseEntity.ok(ApiResponse.success("Note deleted successfully", null));
    }
}
