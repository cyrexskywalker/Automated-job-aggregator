package com.javaproject.vacancy_aggregator.controller;

import com.javaproject.vacancy_aggregator.domain.User;
import com.javaproject.vacancy_aggregator.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public void register(@RequestParam String username,
                         @RequestParam String password) {
        if (userRepo.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }
        String hash = encoder.encode(password);
        userRepo.save(new User(username, hash, "USER"));
    }
}