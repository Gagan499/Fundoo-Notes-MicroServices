package com.example.AuthService.service;

import com.example.AuthService.dto.LoginRequestDTO;
import com.example.AuthService.dto.LoginResponseDTO;
import com.example.AuthService.dto.RegisterRequestDTO;
import com.example.AuthService.dto.RegisterResponseDTO;

public interface UserService {

    //login
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    //register
    RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO);
}
