package com.example.hisync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongResponse {
    private Long id;
    private String youtubeId;
    private String title;
    private String thumbnailUrl;
    private String addedByName;
}