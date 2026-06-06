package com.example.hisync.dto;

import lombok.Data;

@Data
public class BandRequest {
    private String name;
    private String description;
    private Long createdBy;
}