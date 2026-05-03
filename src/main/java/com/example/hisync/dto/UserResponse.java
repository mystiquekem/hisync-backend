// dto/UserResponse.java
package com.example.hisync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String email;
    private String displayName;
    private String role;
}