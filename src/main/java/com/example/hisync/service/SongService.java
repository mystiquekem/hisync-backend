package com.example.hisync.service;

import com.example.hisync.dto.SongRequest;
import com.example.hisync.model.Band;
import com.example.hisync.model.Song;
import com.example.hisync.model.User;
import com.example.hisync.repository.BandRepository;
import com.example.hisync.repository.SongRepository;
import com.example.hisync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepo;
    private final BandRepository bandRepo;
    private final UserRepository userRepo;

    public List<Song> getSongsForBand(Long bandId) {
        return songRepo.findByBandId(bandId);
    }

    public Song addSong(SongRequest req) {
        Band band = bandRepo.findById(req.getBandId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Band not found"));

        User addedBy = userRepo.findById(req.getAddedBy())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (songRepo.existsByBandIdAndYoutubeId(req.getBandId(), req.getYoutubeId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Song already in setlist");

        Song song = new Song();
        song.setBand(band);
        song.setYoutubeId(req.getYoutubeId());
        song.setTitle(req.getTitle());
        song.setThumbnailUrl(req.getThumbnailUrl());
        song.setAddedBy(addedBy);
        return songRepo.save(song);
    }

    public void deleteSong(Long songId) {
        songRepo.deleteById(songId);
    }
}