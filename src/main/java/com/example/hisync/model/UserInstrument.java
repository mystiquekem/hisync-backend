package com.example.hisync.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Entity
@Table(name = "user_instruments")
@Data
@NoArgsConstructor
public class UserInstrument {

    @EmbeddedId
    private UserInstrumentId id = new UserInstrumentId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInstrumentId implements Serializable {
        private Long userId;
        private String instrument;
    }
}