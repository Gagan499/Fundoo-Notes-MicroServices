package com.example.NotesService.exception;

public class EmptyNoteException extends RuntimeException {
    public EmptyNoteException(String message) {
        super(message);
    }
}
