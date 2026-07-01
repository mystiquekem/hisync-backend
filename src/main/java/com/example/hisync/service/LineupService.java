package com.example.hisync.service;

import com.example.hisync.dto.LineupMemberDto;
import com.example.hisync.dto.LineupRequest;
import com.example.hisync.model.*;
import com.example.hisync.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LineupService {

    private final LineupRepository lineupRepo;
    private final LineupMemberRepository lineupMemberRepo;
    private final BandRepository bandRepo;
    private final UserRepository userRepo;
    private final UserInstrumentRepository userInstrumentRepo;

    @Transactional
    public Lineup create(LineupRequest req) {
        Band band = bandRepo.findById(req.getBandId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Band not found"));

        User creator = userRepo.findById(req.getCreatedBy())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Lineup lineup = new Lineup();
        lineup.setBand(band);
        lineup.setSongTitle(req.getSongTitle());
        lineup.setYoutubeId(req.getYoutubeId());
        lineup.setThumbnailUrl(req.getThumbnailUrl());
        lineup.setCreatedBy(creator);
        lineup = lineupRepo.save(lineup);

        saveMembers(lineup, req.getMembers());
        return lineup;
    }

    @Transactional
    public Lineup update(Long lineupId, LineupRequest req) {
        Lineup lineup = lineupRepo.findById(lineupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lineup not found"));

        if (req.getSongTitle() != null && !req.getSongTitle().isBlank())
            lineup.setSongTitle(req.getSongTitle());
        if (req.getYoutubeId() != null)
            lineup.setYoutubeId(req.getYoutubeId());
        if (req.getThumbnailUrl() != null)
            lineup.setThumbnailUrl(req.getThumbnailUrl());

        lineup = lineupRepo.save(lineup);

        if (req.getMembers() != null) {
            lineupMemberRepo.deleteByLineupId(lineupId);
            saveMembers(lineup, req.getMembers());
        }

        return lineup;
    }

    @Transactional
    public void delete(Long lineupId) {
        if (!lineupRepo.existsById(lineupId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lineup not found");
        lineupRepo.deleteById(lineupId);
    }

    public List<Lineup> getByBand(Long bandId) {
        return lineupRepo.findByBandId(bandId);
    }

    @Transactional
    public Lineup getById(Long id) {
        Lineup lineup = lineupRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lineup not found"));
        lineup.getMembers().size(); // force load
        return lineup;
    }

    private void saveMembers(Lineup lineup, List<LineupMemberDto> memberDtos) {
        if (memberDtos == null) return;
        for (LineupMemberDto dto : memberDtos) {
            User user = userRepo.findById(dto.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "User not found: " + dto.getUserId()));

            // Resolve instrument: use dto value if provided,
            // otherwise fall back to user's first instrument
            String instrument = dto.getInstrument();
            if (instrument == null || instrument.isBlank()) {
                List<String> userInstruments =
                        userInstrumentRepo.findInstrumentsByUserId(user.getId());
                instrument = userInstruments.isEmpty() ? "other" : userInstruments.get(0);
            }

            LineupMember member = new LineupMember();
            member.setId(new LineupMember.LineupMemberId(lineup.getId(), user.getId()));
            member.setLineup(lineup);
            member.setUser(user);
            member.setInstrument(instrument);
            lineupMemberRepo.save(member);
        }
    }
}