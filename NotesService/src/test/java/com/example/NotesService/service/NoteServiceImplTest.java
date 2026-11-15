package com.example.NotesService.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.NotesService.dto.NoteRequestDTO;
import com.example.NotesService.entity.Note;
import com.example.NotesService.exception.NoteNotFoundByIdException;
import com.example.NotesService.repository.NoteRepository;
import com.example.NotesService.service.impl.NoteServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteServiceImpl noteService;

    @Test
    void createNoteStoresUserIdAndGeneratesTitleFromContent() {
        NoteRequestDTO request = new NoteRequestDTO();
        request.setContent("These are the first five words in this note");
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        noteService.createNote(request, 42L);

        ArgumentCaptor<Note> noteCaptor = ArgumentCaptor.forClass(Note.class);
        verify(noteRepository).save(noteCaptor.capture());
        assertEquals(42L, noteCaptor.getValue().getUserId());
        assertEquals("These are the first", noteCaptor.getValue().getTitle());
        assertEquals("white", noteCaptor.getValue().getColor());
    }

    @Test
    void accessingAnotherUsersNoteIsReportedAsNotFound() {
        when(noteRepository.findByNoteIdAndUserId(7L, 42L)).thenReturn(Optional.empty());

        assertThrows(NoteNotFoundByIdException.class, () -> noteService.getNoteById(7L, 42L));
    }

    @Test
    void togglingArchiveUpdatesAndSavesTheOwnedNote() {
        Note note = Note.builder().noteId(7L).userId(42L).build();
        when(noteRepository.findByNoteIdAndUserId(7L, 42L)).thenReturn(Optional.of(note));
        when(noteRepository.save(note)).thenReturn(note);

        assertTrue(noteService.toggleArchive(7L, 42L));
        assertTrue(note.isArchived());
        verify(noteRepository).save(note);
    }

    @Test
    void getAllNotesReturnsEveryUserNoteWithoutPaging() {
        Note note = Note.builder().noteId(7L).userId(42L).title("Note").build();
        when(noteRepository.findByUserId(42L)).thenReturn(List.of(note));

        assertEquals(1, noteService.getAllNotesByUserId(42L).size());
    }
}
