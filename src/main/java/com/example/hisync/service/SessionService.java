// service/SessionService.java
package com.example.hisync.service;

import com.example.hisync.dto.SessionRequest;
import com.example.hisync.model.Session;
import com.example.hisync.model.User;
import com.example.hisync.repository.SessionRepository;
import com.example.hisync.repository.UserRepository;
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

    public List<Session> getSessionsForUser(Long userId, LocalDateTime from, LocalDateTime to) {
        return sessionRepo.findByMemberUserIdAndDateBetween(userId, from, to);
    }

    public Session getById(Long id) {
        return sessionRepo.findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));
    }

    public Session create(SessionRequest req) {
        User creator = userRepo.findById(req.getCreatedBy())
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Session session = new Session();
        session.setSongTitle(req.getSongTitle());
        session.setDate(req.getDate());
        session.setCreatedBy(creator);
        return sessionRepo.save(session);
    }
}