// dto/TaskResponse.java
package com.example.hisync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String status;
    private String assignedToName;
}