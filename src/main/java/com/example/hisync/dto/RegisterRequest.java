// dto/RegisterRequest.java
package com.example.hisync.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
}