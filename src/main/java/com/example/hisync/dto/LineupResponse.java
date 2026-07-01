package com.example.hisync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineupResponse {
    private Long id;
    private Long bandId;
    private String songTitle;
    private String youtubeId;
    private String thumbnailUrl;
    private String createdByName;
    private List<LineupMemberDto> members;
}