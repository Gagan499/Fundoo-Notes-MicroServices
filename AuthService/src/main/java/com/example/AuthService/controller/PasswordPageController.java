package com.example.AuthService.controller;

import com.example.AuthService.dto.ForgotPasswordRequestDTO;
import com.example.AuthService.dto.ResetPasswordRequestDTO;
import com.example.AuthService.expection.InvalidCredentialsException;
import com.example.AuthService.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PasswordPageController {

    private final AuthService authService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(Model model) {
        model.addAttribute("request", new ForgotPasswordRequestDTO());
        return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String submitForgotPassword(
            @Valid @ModelAttribute("request") ForgotPasswordRequestDTO request,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "forgot_password";
        }

        authService.processForgotPassword(request);
        model.addAttribute(
                "message",
                "If an account exists with that email, a password reset link has been sent.");
        return "forgot_password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        model.addAttribute("request", new ResetPasswordRequestDTO());
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String submitResetPassword(
            @RequestParam String token,
            @RequestParam String confirmPassword,
            @Valid @ModelAttribute("request") ResetPasswordRequestDTO request,
            BindingResult bindingResult,
            Model model) {
        model.addAttribute("token", token);
        if (bindingResult.hasErrors()) {
            return "reset_password";
        }
        if (!request.getNewPassword().equals(confirmPassword)) {
            model.addAttribute("error", "The passwords do not match.");
            return "reset_password";
        }

        try {
            authService.resetPassword(token, request);
            model.addAttribute("success", true);
        } catch (InvalidCredentialsException exception) {
            model.addAttribute("error", "This reset link is invalid or expired. Request a new link.");
        }
        return "reset_password";
    }
}
