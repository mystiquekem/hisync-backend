package com.example.hisync.service;

import com.example.hisync.dto.SessionRequest;
import com.example.hisync.model.*;
import com.example.hisync.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepo;
    private final UserRepository userRepo;
    private final BandRepository bandRepo;
    private final LineupRepository lineupRepo;

    public List<Session> getSessionsForUser(Long userId, LocalDateTime from, LocalDateTime to) {
        return sessionRepo.findByMemberUserIdAndDateBetween(userId, from, to);
    }

    public List<Session> getSessionsForBand(Long bandId, LocalDateTime from, LocalDateTime to) {
        return sessionRepo.findByBandIdAndDateBetween(bandId, from, to);
    }

    @Transactional
    public Session getById(Long id) {
        return sessionRepo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));
    }

    @Transactional
    public Session create(SessionRequest req) {
        Band band = bandRepo.findById(req.getBandId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Band not found"));

        User creator = userRepo.findById(req.getCreatedBy())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Lineup lineup = lineupRepo.findById(req.getLineupId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Lineup not found"));

        Session session = new Session();
        session.setBand(band);
        session.setLineup(lineup);
        session.setDate(req.getDate());
        session.setDurationMinutes(
                req.getDurationMinutes() != null ? req.getDurationMinutes() : 60);
        session.setCreatedBy(creator);
        return sessionRepo.save(session);
    }

    @Transactional
    public Session update(Long id, SessionRequest req) {
        Session session = sessionRepo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));

        if (req.getLineupId() != null) {
            Lineup lineup = lineupRepo.findById(req.getLineupId())
                    .orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Lineup not found"));
            session.setLineup(lineup);
        }
        if (req.getDate() != null)
            session.setDate(req.getDate());
        if (req.getDurationMinutes() != null)
            session.setDurationMinutes(req.getDurationMinutes());

        return sessionRepo.save(session);
    }

    public void delete(Long id) {
        if (!sessionRepo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
        sessionRepo.deleteById(id);
    }
}