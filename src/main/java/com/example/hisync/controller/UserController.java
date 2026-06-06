package com.example.hisync.controller;

import com.example.hisync.dto.UserResponse;
import com.example.hisync.model.User;
import com.example.hisync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        User user = userRepo.findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (body.containsKey("displayName") && !body.get("displayName").isBlank()) {
            user.setDisplayName(body.get("displayName"));
        }
        user = userRepo.save(user);

        return ResponseEntity.ok(new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getDisplayName(),
            user.getRole().name()
        ));
    }
}