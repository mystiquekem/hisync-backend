package com.example.hisync.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SessionRequest {
    private Long bandId;
    private Long lineupId;
    private LocalDateTime date;
    private Integer durationMinutes;
    private Long createdBy;
}