package com.example.hisync.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Entity
@Table(name = "lineup_members")
@Data
@NoArgsConstructor
public class LineupMember {

    @EmbeddedId
    private LineupMemberId id = new LineupMemberId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lineupId")
    @JoinColumn(name = "lineup_id")
    private Lineup lineup;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "instrument", nullable = false)
    private String instrument;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineupMemberId implements Serializable {
        private Long lineupId;
        private Long userId;
    }
}