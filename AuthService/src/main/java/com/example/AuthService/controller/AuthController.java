package com.example.AuthService.controller;

import com.example.AuthService.dto.ApiResponse;
import com.example.AuthService.dto.ForgotPasswordRequestDTO;
import com.example.AuthService.dto.LoginRequestDTO;
import com.example.AuthService.dto.LoginResponseDTO;
import com.example.AuthService.dto.RegisterRequestDTO;
import com.example.AuthService.dto.RegisterResponseDTO;
import com.example.AuthService.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponseDTO>> register(
            @Valid @RequestBody RegisterRequestDTO request) {
        RegisterResponseDTO result = authService.registerUser(request);
        log.info("Registration request completed");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", result));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO result = authService.loginUser(request);
        log.info("Login request completed");
        return ResponseEntity.ok(ApiResponse.success("User login successful", result));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO request) {
        authService.processForgotPassword(request);
        log.info("Password reset request processed");
        return ResponseEntity.ok(ApiResponse.success(
                "If an account exists with this email, a password reset link has been sent", null));
    }

}
