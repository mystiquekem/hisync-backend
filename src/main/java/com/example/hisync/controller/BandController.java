package com.example.hisync.controller;

import com.example.hisync.dto.BandRequest;
import com.example.hisync.dto.BandResponse;
import com.example.hisync.model.Band;
import com.example.hisync.repository.BandMemberRepository;
import com.example.hisync.service.BandService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bands")
@RequiredArgsConstructor
public class BandController {

    private final BandService bandService;
    private final BandMemberRepository bandMemberRepo;

    @PostMapping
    @Transactional
    public ResponseEntity<BandResponse> create(@RequestBody BandRequest req) {
        return ResponseEntity.status(201).body(toResponse(bandService.create(req)));
    }

    @PostMapping("/join")
    @Transactional
    public ResponseEntity<BandResponse> join(@RequestBody Map<String, Object> body) {
        String code = (String) body.get("inviteCode");
        Long userId = Long.valueOf(body.get("userId").toString());
        return ResponseEntity.ok(toResponse(bandService.joinByInviteCode(code, userId)));
    }

    @GetMapping
    @Transactional
    public ResponseEntity<List<BandResponse>> getMyBands(@RequestParam Long userId) {
        return ResponseEntity.ok(
            bandService.getBandsForUser(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<BandResponse> getBand(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(bandService.getById(id)));
    }

    private BandResponse toResponse(Band band) {
        List<BandResponse.MemberDto> members = bandMemberRepo
            .findByBandId(band.getId())
            .stream()
            .map(m -> new BandResponse.MemberDto(
                m.getUser().getId(),
                m.getUser().getDisplayName(),
                m.getUser().getEmail(),
                m.getRole().name()
            ))
            .collect(Collectors.toList());

        return new BandResponse(
            band.getId(),
            band.getName(),
            band.getDescription(),
            band.getInviteCode(),
            members
        );
    }
}