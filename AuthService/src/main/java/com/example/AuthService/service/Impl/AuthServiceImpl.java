package com.example.AuthService.service.Impl;

import com.example.AuthService.dto.ForgotPasswordRequestDTO;
import com.example.AuthService.dto.LoginRequestDTO;
import com.example.AuthService.dto.LoginResponseDTO;
import com.example.AuthService.dto.RegisterRequestDTO;
import com.example.AuthService.dto.RegisterResponseDTO;
import com.example.AuthService.dto.ResetPasswordRequestDTO;
import com.example.AuthService.entity.Users;
import com.example.AuthService.expection.InvalidCredentialsException;
import com.example.AuthService.expection.UserAlreadyExists;
import com.example.AuthService.repository.UserRepository;
import com.example.AuthService.service.AuthService;
import com.example.AuthService.service.EmailService;
import com.example.AuthService.utils.Jwtutils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Jwtutils jwtUtils;
    private final EmailService emailService;

    @Value("${app.frontend.reset-password-url:http://localhost:8080/reset-password}")
    private String resetPasswordUrl;

    @Override
    public RegisterResponseDTO registerUser(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new UserAlreadyExists("User already exists with email: " + registerRequestDTO.getEmail());
        }

        Users user = new Users(
                registerRequestDTO.getFirstName(),
                registerRequestDTO.getLastName(),
                registerRequestDTO.getEmail(),
                passwordEncoder.encode(registerRequestDTO.getPassword()));
        Users savedUser = userRepository.save(user);
        return new RegisterResponseDTO(
                savedUser.getUserId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail());
    }

    @Override
    public LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO) {
        Users user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtils.generateToken(user.getEmail(), user.getUserId());
        log.info("User login completed");
        return new LoginResponseDTO(
                token,
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail());
    }

    @Override
    public void processForgotPassword(ForgotPasswordRequestDTO request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String resetToken = jwtUtils.generateResetToken(user.getEmail(), user.getUserId());
            String resetLink = resetPasswordUrl + "?token=" + resetToken;
            emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
        });
        log.info("Password reset request processed");
    }

    @Override
    public void resetPassword(String token, ResetPasswordRequestDTO request) {
        Long userId = jwtUtils.extractUserIdFromResetToken(token);
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid password reset token"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password reset completed");
    }
}
