package com.example.AuthService.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private String message;
    private T data;
    private String error;
    private List<String> errors;

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data, null, null);
    }

    public static <T> ApiResponse<T> error(String message, String error) {
        return new ApiResponse<>(message, null, error, null);
    }

    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        return new ApiResponse<>(message, null, null, errors);
    }
}