package com.example.hisync.service;

import com.example.hisync.dto.BandRequest;
import com.example.hisync.model.Band;
import com.example.hisync.model.BandMember;
import com.example.hisync.model.User;
import com.example.hisync.repository.BandMemberRepository;
import com.example.hisync.repository.BandRepository;
import com.example.hisync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BandService {

    private final BandRepository bandRepo;
    private final BandMemberRepository bandMemberRepo;
    private final UserRepository userRepo;

    @Transactional
    public Band create(BandRequest req) {
        User creator = userRepo.findById(req.getCreatedBy())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Band band = new Band();
        band.setName(req.getName());
        band.setDescription(req.getDescription());
        band.setInviteCode(generateInviteCode());
        band.setCreatedBy(creator);
        band = bandRepo.save(band);
        bandRepo.flush();

        // Auto-assign creator as leader trong band
        BandMember.BandMemberId memberId = new BandMember.BandMemberId(band.getId(), creator.getId());
        BandMember membership = new BandMember();
        membership.setId(memberId);
        membership.setBand(band);
        membership.setUser(creator);
        membership.setRole(User.Role.leader);
        bandMemberRepo.save(membership);

        // Update global role của user thành leader
        creator.setRole(User.Role.leader);
        userRepo.save(creator);

        return band;
    }

    @Transactional
    public Band joinByInviteCode(String code, Long userId) {
        Band band = bandRepo.findByInviteCode(code)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid invite code"));

        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (bandMemberRepo.existsByBandIdAndUserId(band.getId(), userId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already a member");

        BandMember.BandMemberId memberId = new BandMember.BandMemberId(band.getId(), userId);
        BandMember membership = new BandMember();
        membership.setId(memberId);
        membership.setBand(band);
        membership.setUser(user);
        membership.setRole(User.Role.member);
        bandMemberRepo.save(membership);

        return band;
    }

    public List<Band> getBandsForUser(Long userId) {
        return bandRepo.findByMemberUserId(userId);
    }

    @Transactional
    public Band getById(Long id) {
        return bandRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Band not found"));
    }

    private String generateInviteCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}