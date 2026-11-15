package com.example.AuthService.service.Impl;

import com.example.AuthService.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${spring.mail.username:}")
    private String sender;

    @Override
    public void sendPasswordResetEmail(String email, String resetLink) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null || sender.isBlank()) {
            throw new IllegalStateException("Email delivery is not configured");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(email);
        message.setSubject("Reset your password");
        message.setText("Use the following link to reset your password:\n" + resetLink);
        mailSender.send(message);
        log.info("Password reset email sent");
    }
}
