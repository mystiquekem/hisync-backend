// dto/SessionRequest.java
package com.example.hisync.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SessionRequest {
    private String songTitle;
    private LocalDateTime date;
    private Long createdBy;
}