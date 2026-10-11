package com.example.AuthService.controller;

import com.example.AuthService.dto.*;
import com.example.AuthService.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponseDTO>> register(@RequestBody RegisterRequestDTO registerRequestDTO){
        RegisterResponseDTO result = userService.register(registerRequestDTO);
        ApiResponse<RegisterResponseDTO> response = ApiResponse.success("User registered successfully",result);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody LoginRequestDTO loginRequestDTO){
        LoginResponseDTO result = userService.login(loginRequestDTO);
        ApiResponse<LoginResponseDTO> response = ApiResponse.success("User login successfully",result);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
