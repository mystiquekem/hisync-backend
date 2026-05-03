package com.example.hisync.controller;

import com.example.hisync.dto.LoginRequest;
import com.example.hisync.dto.RegisterRequest;
import com.example.hisync.dto.UserResponse;
import com.example.hisync.model.User;
import com.example.hisync.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest req) {
        User user = authService.register(req);
        return ResponseEntity.status(201).body(
            new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRole().name()
            )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest req) {
        User user = authService.login(req);
        return ResponseEntity.ok(
            new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRole().name()
            )
        );
    }
}