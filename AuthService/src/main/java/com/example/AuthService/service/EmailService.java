package com.example.AuthService.service;

public interface EmailService {
    void sendPasswordResetEmail(String email, String resetLink);
}
