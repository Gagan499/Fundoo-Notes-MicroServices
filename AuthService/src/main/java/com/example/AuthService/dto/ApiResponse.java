package com.example.AuthService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiResponse<T> {
    private final String message;
    private final boolean success;
    private final T data;
    private final List<String> errors;

    public ApiResponse(String message, boolean success, T data, List<String> errors) {
        this.message = message;
        this.success = success;
        this.data = data;
        this.errors = errors;
    }

    public static  <T> ApiResponse<T> success(String message,T data){
        return  new ApiResponse<>(message,true,data,null);
    }

    public static  <T> ApiResponse<T> error(String message){
        return  new ApiResponse<>(message,false,null,null);
    }

    public static  <T> ApiResponse<T> error(String message,List<String> errors){
        return  new ApiResponse<>(message,false,null,errors);
    }


}
