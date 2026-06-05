package com.example.hisync.controller;

import com.example.hisync.dto.UserResponse;
import com.example.hisync.model.User;
import com.example.hisync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepo;

    // Lấy danh sách tất cả users
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepo.findAll().stream()
            .map(u -> new UserResponse(
                u.getId(),
                u.getEmail(),
                u.getDisplayName(),
                u.getRole().name()
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // Đổi role của user
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<UserResponse> updateRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        User user = userRepo.findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String newRole = body.get("role");
        try {
            user.setRole(User.Role.valueOf(newRole));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Invalid role: " + newRole + ". Must be member, leader, or admin");
        }

        userRepo.save(user);
        return ResponseEntity.ok(new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getDisplayName(),
            user.getRole().name()
        ));
    }

    // Xóa user
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        userRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}