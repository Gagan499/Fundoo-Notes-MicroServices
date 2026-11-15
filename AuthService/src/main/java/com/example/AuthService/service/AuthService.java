package com.example.AuthService.service;

import com.example.AuthService.dto.ForgotPasswordRequestDTO;
import com.example.AuthService.dto.LoginRequestDTO;
import com.example.AuthService.dto.LoginResponseDTO;
import com.example.AuthService.dto.RegisterRequestDTO;
import com.example.AuthService.dto.RegisterResponseDTO;
import com.example.AuthService.dto.ResetPasswordRequestDTO;

public interface AuthService {
    RegisterResponseDTO registerUser(RegisterRequestDTO registerRequestDTO);
    LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO);
    void processForgotPassword(ForgotPasswordRequestDTO request);
    void resetPassword(String token, ResetPasswordRequestDTO request);
}
