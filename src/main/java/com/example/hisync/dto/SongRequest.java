package com.example.hisync.dto;

import lombok.Data;

@Data
public class SongRequest {
    private Long bandId;
    private String youtubeId;
    private String title;
    private String thumbnailUrl;
    private Long addedBy;
}