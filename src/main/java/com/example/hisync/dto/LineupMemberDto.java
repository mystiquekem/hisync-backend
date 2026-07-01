package com.example.hisync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineupMemberDto {
    private Long userId;
    private String displayName;
    private String instrument;
}