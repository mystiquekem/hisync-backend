package com.example.hisync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {
    private Long id;
    private String songTitle;
    private LocalDateTime date;
    private String createdByName;
    private List<MemberDto> members;
    private List<TaskResponse> tasks;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberDto {
        private Long userId;
        private String displayName;
        private String instrument;
    }
}