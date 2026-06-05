package com.example.hisync.controller;

import com.example.hisync.dto.TaskResponse;
import com.example.hisync.model.Task;
import com.example.hisync.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepo;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getMyTasks(@RequestParam Long userId) {
        return ResponseEntity.ok(
            taskRepo.findByAssignedToId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList())
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Task task = taskRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        task.setStatus(Task.Status.valueOf(body.get("status")));
        return ResponseEntity.ok(toResponse(taskRepo.save(task)));
    }

    @PatchMapping("/{id}/recording")
    public ResponseEntity<TaskResponse> submitRecording(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Task task = taskRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        task.setRecordingUrl(body.get("recordingUrl"));
        task.setStatus(Task.Status.done);
        return ResponseEntity.ok(toResponse(taskRepo.save(task)));
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getSession() != null ? task.getSession().getId() : null,
            task.getTitle(),
            task.getStatus().name(),
            task.getAssignedTo() != null ? task.getAssignedTo().getDisplayName() : "Unknown",
            task.getRecordingUrl()
        );
    }
}