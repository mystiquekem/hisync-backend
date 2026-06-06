package com.example.hisync.controller;

import com.example.hisync.dto.SessionRequest;
import com.example.hisync.dto.SessionResponse;
import com.example.hisync.dto.TaskResponse;
import com.example.hisync.model.Session;
import com.example.hisync.repository.TaskRepository;
import com.example.hisync.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final TaskRepository taskRepo; // thêm dòng này

    @GetMapping
    public ResponseEntity<List<SessionResponse>> getSessions(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        List<SessionResponse> response = sessionService
            .getSessionsForUser(userId, from, to)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionResponse> getSession(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(sessionService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<SessionResponse> create(@RequestBody SessionRequest req) {
        return ResponseEntity.status(201).body(toResponse(sessionService.create(req)));
    }

    // Convert Session entity → SessionResponse DTO
    private SessionResponse toResponse(Session session) {
    // Map members
        List<SessionResponse.MemberDto> members = session.getMembers().stream()
            .map(m -> new SessionResponse.MemberDto(
                m.getUser().getId(),
                m.getUser().getDisplayName(),
                m.getInstrument()
            ))
            .collect(Collectors.toList());

        // Map tasks
        List<TaskResponse> tasks = session.getMembers().isEmpty() ? List.of() :
            taskRepo.findBySessionId(session.getId()).stream()
                .map(t -> new TaskResponse(
                    t.getId(),
                    session.getId(),
                    t.getTitle(), 
                    t.getStatus().name(),
                    t.getAssignedTo() != null ? t.getAssignedTo().getDisplayName() : "Unknown",
                    t.getRecordingUrl()
                ))
                .collect(Collectors.toList());

        return new SessionResponse(
            session.getId(),
            session.getSongTitle(),
            session.getDate(),
            session.getCreatedBy() != null ? session.getCreatedBy().getDisplayName() : "Unknown",
            members,
            tasks
        );
    }
}