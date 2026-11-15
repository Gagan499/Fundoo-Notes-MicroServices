package com.example.NotesService.exception;

public class AlreadyNoteIsTrashed extends RuntimeException {
    public AlreadyNoteIsTrashed(String message) {
        super(message);
    }
}
