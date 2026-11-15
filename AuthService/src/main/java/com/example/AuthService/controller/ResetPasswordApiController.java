package com.example.AuthService.controller;

import com.example.AuthService.dto.ApiResponse;
import com.example.AuthService.dto.ResetPasswordRequestDTO;
import com.example.AuthService.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/reset-password", "/api/users/reset-password"})
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordApiController {

    private final AuthService authService;

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam String token,
            @Valid @RequestBody ResetPasswordRequestDTO request) {
        authService.resetPassword(token, request);
        log.info("Password reset completed through the API");
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
    }
}
