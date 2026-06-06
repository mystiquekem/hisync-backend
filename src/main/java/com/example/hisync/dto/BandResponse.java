package com.example.hisync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BandResponse {
    private Long id;
    private String name;
    private String description;
    private String inviteCode;
    private List<MemberDto> members;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberDto {
        private Long userId;
        private String displayName;
        private String email;
        private String role;
    }
}