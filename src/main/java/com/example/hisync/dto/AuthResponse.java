package com.example.hisync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Returned by login and register. Has the same user fields as UserResponse
 * (so existing frontend code keeps working) plus the login token.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType;
    private long expiresIn;   // seconds
    private Long userId;
    private String email;
    private String displayName;
    private String role;
    private List<String> instruments;

    public static AuthResponse of(String token, long expiresIn, UserResponse user) {
        return new AuthResponse(token, "Bearer", expiresIn,
                user.getUserId(), user.getEmail(), user.getDisplayName(),
                user.getRole(), user.getInstruments());
    }
}
