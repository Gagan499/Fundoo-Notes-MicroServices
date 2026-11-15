package com.example.AuthService.expection;

public class UserAlreadyExists extends RuntimeException {
    public UserAlreadyExists(String message){
        super(message);
    }
}
