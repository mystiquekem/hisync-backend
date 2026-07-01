package com.example.hisync.controller;

import com.example.hisync.dto.LineupMemberDto;
import com.example.hisync.dto.LineupRequest;
import com.example.hisync.dto.LineupResponse;
import com.example.hisync.model.Lineup;
import com.example.hisync.repository.LineupMemberRepository;
import com.example.hisync.service.LineupService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lineups")
@RequiredArgsConstructor
public class LineupController {

    private final LineupService lineupService;
    private final LineupMemberRepository lineupMemberRepo;

    @PostMapping
    @Transactional
    public ResponseEntity<LineupResponse> create(@RequestBody LineupRequest req) {
        return ResponseEntity.status(201).body(toResponse(lineupService.create(req)));
    }

    @GetMapping
    @Transactional
    public ResponseEntity<List<LineupResponse>> getByBand(@RequestParam Long bandId) {
        return ResponseEntity.ok(
                lineupService.getByBand(bandId).stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<LineupResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(lineupService.getById(id)));
    }

    @PatchMapping("/{id}")
    @Transactional
    public ResponseEntity<LineupResponse> update(
            @PathVariable Long id,
            @RequestBody LineupRequest req) {
        return ResponseEntity.ok(toResponse(lineupService.update(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lineupService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private LineupResponse toResponse(Lineup lineup) {
        List<LineupMemberDto> members = lineupMemberRepo
                .findByLineupId(lineup.getId())
                .stream()
                .map(m -> new LineupMemberDto(
                        m.getUser().getId(),
                        m.getUser().getDisplayName(),
                        m.getInstrument()
                ))
                .collect(Collectors.toList());

        return new LineupResponse(
                lineup.getId(),
                lineup.getBand().getId(),
                lineup.getSongTitle(),
                lineup.getYoutubeId(),
                lineup.getThumbnailUrl(),
                lineup.getCreatedBy() != null
                        ? lineup.getCreatedBy().getDisplayName() : "Unknown",
                members
        );
    }
}