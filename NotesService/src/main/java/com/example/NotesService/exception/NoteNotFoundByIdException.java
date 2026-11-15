package com.example.NotesService.exception;

public class NoteNotFoundByIdException extends RuntimeException {
    public NoteNotFoundByIdException(String message) {
        super(message);
    }
}
