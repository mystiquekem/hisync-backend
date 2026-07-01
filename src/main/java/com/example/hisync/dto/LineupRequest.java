package com.example.hisync.dto;

import lombok.Data;
import java.util.List;

@Data
public class LineupRequest {
    private Long bandId;
    private String songTitle;
    private String youtubeId;
    private String thumbnailUrl;
    private Long createdBy;
    private List<LineupMemberDto> members;
}