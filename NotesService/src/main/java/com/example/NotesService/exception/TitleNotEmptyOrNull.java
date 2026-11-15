package com.example.NotesService.exception;

public class TitleNotEmptyOrNull extends RuntimeException {
    public TitleNotEmptyOrNull(String message) {
        super(message);
    }
}
