// dto/SessionResponse.java
package com.example.hisync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SessionResponse {
    private Long id;
    private String songTitle;
    private LocalDateTime date;
    private String createdByName;
}