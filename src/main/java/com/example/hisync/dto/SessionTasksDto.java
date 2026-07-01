package com.example.hisync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionTasksDto {
    private Long sessionId;
    private String songTitle;
    private LocalDateTime sessionDate;
    private List<TaskResponse> tasks;
}