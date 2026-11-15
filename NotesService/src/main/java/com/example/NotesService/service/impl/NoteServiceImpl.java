package com.example.NotesService.service.impl;

import com.example.NotesService.dto.NoteRequestDTO;
import com.example.NotesService.dto.NoteResponseDTO;
import com.example.NotesService.entity.Note;
import com.example.NotesService.exception.AlreadyNoteIsTrashed;
import com.example.NotesService.exception.EmptyNoteException;
import com.example.NotesService.exception.NoteNotFoundByIdException;
import com.example.NotesService.exception.TitleNotEmptyOrNull;
import com.example.NotesService.repository.NoteRepository;
import com.example.NotesService.service.NoteService;
import java.util.Arrays;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    public NoteServiceImpl(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    @Caching(evict = {
        @CacheEvict(cacheNames = "notesByUser", key = "#p1"),
        @CacheEvict(cacheNames = "archivedNotesByUser", key = "#p1")
    })
    public NoteResponseDTO createNote(NoteRequestDTO request, Long userId) {
        if (!StringUtils.hasText(request.getTitle()) && !StringUtils.hasText(request.getContent())) {
            throw new EmptyNoteException("Title and content cannot both be blank");
        }

        String title = request.getTitle();
        if (!StringUtils.hasText(title)) {
            String[] words = request.getContent().trim().split("\\s+");
            title = String.join(" ", Arrays.copyOf(words, Math.min(4, words.length)));
        }

        Note note = Note.builder()
                .title(title)
                .content(request.getContent())
                .color(StringUtils.hasText(request.getColor()) ? request.getColor() : "white")
                .userId(userId)
                .build();
        return toResponse(noteRepository.save(note));
    }

    @Override
    @Cacheable(cacheNames = "notesByUser", key = "#p0")
    public List<NoteResponseDTO> getAllNotesByUserId(Long userId) {
        return noteRepository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    @Override
    @Cacheable(cacheNames = "archivedNotesByUser", key = "#p0")
    public List<NoteResponseDTO> getArchivedNotesByUserId(Long userId) {
        return noteRepository.findByUserIdAndArchivedTrueAndTrashedFalse(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public NoteResponseDTO getNoteById(Long noteId, Long userId) {
        Note note = findOwnedNote(noteId, userId);
        if (note.isTrashed()) {
            throw new AlreadyNoteIsTrashed("This note is in the trash");
        }
        return toResponse(note);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = "notesByUser", key = "#p2"),
        @CacheEvict(cacheNames = "archivedNotesByUser", key = "#p2")
    })
    public NoteResponseDTO editNote(NoteRequestDTO request, Long noteId, Long userId) {
        Note note = findOwnedNote(noteId, userId);
        if (request.getTitle() != null) {
            if (!StringUtils.hasText(request.getTitle())) {
                throw new TitleNotEmptyOrNull("Title cannot be blank");
            }
            note.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            note.setContent(request.getContent());
        }
        if (request.getColor() != null) {
            note.setColor(request.getColor());
        }
        return toResponse(noteRepository.save(note));
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = "notesByUser", key = "#p1"),
        @CacheEvict(cacheNames = "archivedNotesByUser", key = "#p1")
    })
    public boolean toggleTrash(Long noteId, Long userId) {
        Note note = findOwnedNote(noteId, userId);
        note.setTrashed(!note.isTrashed());
        noteRepository.save(note);
        return note.isTrashed();
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = "notesByUser", key = "#p1"),
        @CacheEvict(cacheNames = "archivedNotesByUser", key = "#p1")
    })
    public boolean toggleArchive(Long noteId, Long userId) {
        Note note = findOwnedNote(noteId, userId);
        note.setArchived(!note.isArchived());
        noteRepository.save(note);
        return note.isArchived();
    }

    @Override
    @Caching(evict = {
        @CacheEvict(cacheNames = "notesByUser", key = "#p1"),
        @CacheEvict(cacheNames = "archivedNotesByUser", key = "#p1")
    })
    public void deleteNoteById(Long noteId, Long userId) {
        noteRepository.delete(findOwnedNote(noteId, userId));
    }

    private Note findOwnedNote(Long noteId, Long userId) {
        return noteRepository.findByNoteIdAndUserId(noteId, userId)
                .orElseThrow(() -> new NoteNotFoundByIdException("Note not found"));
    }

    private NoteResponseDTO toResponse(Note note) {
        return new NoteResponseDTO(
                note.getNoteId(),
                note.getTitle(),
                note.getContent(),
                note.getColor(),
                note.isArchived(),
                note.isTrashed(),
                note.getCreatedAt(),
                note.getUpdatedAt());
    }
}
