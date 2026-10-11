package com.example.AuthService.service.Impl;

import com.example.AuthService.dto.LoginRequestDTO;
import com.example.AuthService.dto.LoginResponseDTO;
import com.example.AuthService.dto.RegisterRequestDTO;
import com.example.AuthService.dto.RegisterResponseDTO;
import com.example.AuthService.entity.Users;
import com.example.AuthService.exception.InvalidCredentialsException;
import com.example.AuthService.exception.UserAlreadyExistsException;
import com.example.AuthService.repository.UserRepository;
import com.example.AuthService.service.UserService;
import com.example.AuthService.util.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public UserServiceImpl(UserRepository userRepository,PasswordEncoder passwordEncoder,JwtUtils jwtUtils){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils =jwtUtils;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Users user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        String token = jwtUtils.generateToken(user.getEmail());
        return new LoginResponseDTO(
                token,
                user.getUserId(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    @Override
    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        if(userRepository.existsByEmail(registerRequestDTO.getEmail())){
            throw new UserAlreadyExistsException("User already exits by these email");
        }
        Users u = Users.builder()
                    .firstName(registerRequestDTO.getFirstName())
                    .lastName(registerRequestDTO.getLastName())
                    .email(registerRequestDTO.getEmail())
                    .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                    .build();
        Users saveduser = userRepository.save(u);
        return new RegisterResponseDTO(
                saveduser.getUserId(),
                saveduser.getFirstName(),
                saveduser.getLastName(),
                saveduser.getEmail()
        );
    }
}
