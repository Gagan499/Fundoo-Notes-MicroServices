package com.example.NotesService.repository;

import com.example.NotesService.entity.Note;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserId(Long userId);

    List<Note> findByUserIdAndArchivedTrueAndTrashedFalse(Long userId);

    Optional<Note> findByNoteIdAndUserId(Long noteId, Long userId);
}
