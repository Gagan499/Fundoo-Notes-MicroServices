package com.example.AuthService.exception;

public class UserAlreadyExistsException extends RuntimeException {
    private String message;
    public UserAlreadyExistsException(String message){
        super(message);
    }
}
