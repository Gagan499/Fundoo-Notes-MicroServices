package com.example.NotesService.service;

import com.example.NotesService.dto.NoteRequestDTO;
import com.example.NotesService.dto.NoteResponseDTO;
import java.util.List;

public interface NoteService {
    NoteResponseDTO createNote(NoteRequestDTO request, Long userId);

    List<NoteResponseDTO> getAllNotesByUserId(Long userId);

    List<NoteResponseDTO> getArchivedNotesByUserId(Long userId);

    NoteResponseDTO getNoteById(Long noteId, Long userId);

    NoteResponseDTO editNote(NoteRequestDTO request, Long noteId, Long userId);

    boolean toggleTrash(Long noteId, Long userId);

    boolean toggleArchive(Long noteId, Long userId);

    void deleteNoteById(Long noteId, Long userId);
}
