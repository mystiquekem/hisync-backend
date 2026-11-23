package com.example.hisync.controller;

import com.example.hisync.dto.AuthResponse;
import com.example.hisync.dto.ForgotPasswordRequest;
import com.example.hisync.dto.LoginRequest;
import com.example.hisync.dto.RegisterRequest;
import com.example.hisync.dto.ResetPasswordRequest;
import com.example.hisync.dto.UserResponse;
import com.example.hisync.model.User;
import com.example.hisync.security.JwtService;
import com.example.hisync.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest req) {
        User user = authService.register(req);
        return ResponseEntity.status(201).body(toAuthResponse(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        User user = authService.login(req);
        return ResponseEntity.ok(toAuthResponse(user));
    }

    /** Returns whoever the token in the Authorization header belongs to. */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(authService.toResponse(user));
    }

    private AuthResponse toAuthResponse(User user) {
        return AuthResponse.of(
                jwtService.generateToken(user),
                jwtService.getExpiresInSeconds(),
                authService.toResponse(user));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        authService.sendOtp(req.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest req) {
        authService.resetPassword(req.getEmail(), req.getOtp(), req.getNewPassword());
        return ResponseEntity.ok().build();
    }
}