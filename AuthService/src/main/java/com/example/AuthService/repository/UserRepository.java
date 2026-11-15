package com.example.AuthService.repository;

import java.util.Optional;
import com.example.AuthService.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String email);
}