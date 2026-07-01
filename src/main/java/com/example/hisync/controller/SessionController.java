package com.example.hisync.controller;

import com.example.hisync.dto.LineupMemberDto;
import com.example.hisync.dto.SessionRequest;
import com.example.hisync.dto.SessionResponse;
import com.example.hisync.dto.TaskResponse;
import com.example.hisync.model.Session;
import com.example.hisync.repository.LineupMemberRepository;
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
    private final TaskRepository taskRepo;
    private final LineupMemberRepository lineupMemberRepo;

    // Members: sessions they are part of via lineup membership
    @GetMapping
    public ResponseEntity<List<SessionResponse>> getSessions(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bandId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        List<Session> sessions;
        if (bandId != null) {
            sessions = sessionService.getSessionsForBand(bandId, from, to);
        } else if (userId != null) {
            sessions = sessionService.getSessionsForUser(userId, from, to);
        } else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                sessions.stream().map(this::toResponse).collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionResponse> getSession(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(sessionService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<SessionResponse> create(@RequestBody SessionRequest req) {
        return ResponseEntity.status(201).body(toResponse(sessionService.create(req)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SessionResponse> update(
            @PathVariable Long id,
            @RequestBody SessionRequest req) {
        return ResponseEntity.ok(toResponse(sessionService.update(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sessionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private SessionResponse toResponse(Session session) {
        List<LineupMemberDto> members = List.of();
        String songTitle = "";
        String thumbnailUrl = null;
        Long lineupId = null;

        if (session.getLineup() != null) {
            lineupId = session.getLineup().getId();
            songTitle = session.getLineup().getSongTitle();
            thumbnailUrl = session.getLineup().getThumbnailUrl();
            members = lineupMemberRepo.findByLineupId(lineupId)
                    .stream()
                    .map(m -> new LineupMemberDto(
                            m.getUser().getId(),
                            m.getUser().getDisplayName(),
                            m.getInstrument()
                    ))
                    .collect(Collectors.toList());
        }

        List<TaskResponse> tasks = taskRepo.findBySessionId(session.getId())
            .stream()
            .map(t -> new TaskResponse(
                    t.getId(),
                    session.getId(),
                    t.getTitle(),
                    t.getStatus().name(),
                    t.getAssignedTo() != null
                            ? t.getAssignedTo().getDisplayName() : "Unknown",
                    t.getRecordingUrl(),
                    session.getLineup() != null
                            ? session.getLineup().getSongTitle() : "",  // sessionSong
                    session.getDate() != null
                            ? session.getDate().toString() : ""         // sessionDate
            ))
            .collect(Collectors.toList());
        return new SessionResponse(
                session.getId(),
                lineupId,
                songTitle,
                thumbnailUrl,
                session.getDate(),
                session.getDurationMinutes(),
                session.getCreatedBy() != null
                        ? session.getCreatedBy().getDisplayName() : "Unknown",
                members,
                tasks
        );
    }
}