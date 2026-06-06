package com.example.hisync.service;

import com.example.hisync.dto.LoginRequest;
import com.example.hisync.dto.RegisterRequest;
import com.example.hisync.model.User;
import com.example.hisync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final OtpStore otpStore;

    public User register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");

        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setDisplayName(
            req.getDisplayName() != null && !req.getDisplayName().isBlank()
                ? req.getDisplayName()
                : req.getEmail().split("@")[0]
        );
        return userRepo.save(user);
    }

    public User login(LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password");

        return user;
    }

    public void sendOtp(String email) {
        userRepo.findByEmail(email)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not registered"));

        String otp = String.format("%06d", new SecureRandom().nextInt(999999));
        otpStore.save(email, otp);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("hisync — Password Reset OTP");
        message.setText(
            "Your OTP to reset your hisync password is:\n\n" +
            otp + "\n\n" +
            "This code expires in 15 minutes.\n" +
            "If you didn't request this, ignore this email."
        );
        mailSender.send(message);
    }

    public void resetPassword(String email, String otp, String newPassword) {
        if (!otpStore.verify(email, otp))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired OTP");

        User user = userRepo.findByEmail(email)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (newPassword.length() < 6)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password too short");

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        otpStore.invalidate(email);
    }
}