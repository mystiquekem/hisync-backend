package com.example.hisync.controller;

import com.example.hisync.dto.SongRequest;
import com.example.hisync.dto.SongResponse;
import com.example.hisync.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @GetMapping
    public ResponseEntity<List<SongResponse>> getSongs(@RequestParam Long bandId) {
        return ResponseEntity.ok(
            songService.getSongsForBand(bandId)
                .stream()
                .map(s -> new SongResponse(
                    s.getId(),
                    s.getYoutubeId(),
                    s.getTitle(),
                    s.getThumbnailUrl(),
                    s.getAddedBy() != null ? s.getAddedBy().getDisplayName() : "Unknown"
                ))
                .collect(Collectors.toList())
        );
    }

    @PostMapping
    public ResponseEntity<SongResponse> addSong(@RequestBody SongRequest req) {
        var song = songService.addSong(req);
        return ResponseEntity.status(201).body(new SongResponse(
            song.getId(),
            song.getYoutubeId(),
            song.getTitle(),
            song.getThumbnailUrl(),
            song.getAddedBy() != null ? song.getAddedBy().getDisplayName() : "Unknown"
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }
}