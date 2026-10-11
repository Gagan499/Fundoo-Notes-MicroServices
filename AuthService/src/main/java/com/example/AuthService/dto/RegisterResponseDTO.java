package com.example.AuthService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterResponseDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
}
