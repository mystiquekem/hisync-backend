package com.example.hisync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private Long sessionId;
    private String title;
    private String status;
    private String assignedToName;
    private String recordingUrl;
    private String sessionSong;   // new
    private String sessionDate;   // new
}