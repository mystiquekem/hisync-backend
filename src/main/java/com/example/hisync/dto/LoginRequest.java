// dto/LoginRequest.java
package com.example.hisync.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}