package com.example.hisync.controller;

import com.example.hisync.dto.UserResponse;
import com.example.hisync.model.User;
import com.example.hisync.model.UserInstrument;
import com.example.hisync.repository.UserInstrumentRepository;
import com.example.hisync.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;
    private final UserInstrumentRepository instrumentRepo;

    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        User user = userRepo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (body.containsKey("displayName")) {
            String name = (String) body.get("displayName");
            if (name != null && !name.isBlank()) {
                user.setDisplayName(name);
            }
        }
        user = userRepo.save(user);

        if (body.containsKey("instruments")) {
            @SuppressWarnings("unchecked")
            List<String> incoming = (List<String>) body.get("instruments");

            // Replace all instruments for this user
            instrumentRepo.deleteByUserId(id);

            if (incoming != null) {
                for (String instrument : incoming) {
                    if (instrument == null || instrument.isBlank()) continue;
                    UserInstrument ui = new UserInstrument();
                    ui.setId(new UserInstrument.UserInstrumentId(id, instrument.trim().toLowerCase()));
                    ui.setUser(user);
                    instrumentRepo.save(ui);
                }
            }
        }

        List<String> instruments = instrumentRepo.findInstrumentsByUserId(id);

        return ResponseEntity.ok(new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRole().name(),
                instruments
        ));
    }

    @Transactional
    @GetMapping("/{id}/instruments")
    public ResponseEntity<List<String>> getInstruments(@PathVariable Long id) {
        if (!userRepo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        return ResponseEntity.ok(instrumentRepo.findInstrumentsByUserId(id));
    }
}