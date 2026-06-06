package com.example.hisync.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "band_members")
@Data
@NoArgsConstructor
public class BandMember {

    @EmbeddedId
    private BandMemberId id = new BandMemberId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bandId")
    @JoinColumn(name = "band_id")
    private Band band;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private User.Role role = User.Role.member;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt = LocalDateTime.now();

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BandMemberId implements Serializable {
        private Long bandId;
        private Long userId;
    }
}