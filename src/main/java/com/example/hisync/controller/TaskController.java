package com.example.hisync.controller;

import com.example.hisync.dto.SessionTasksDto;
import com.example.hisync.dto.TaskResponse;
import com.example.hisync.model.Session;
import com.example.hisync.model.Task;
import com.example.hisync.model.User;
import com.example.hisync.repository.SessionRepository;
import com.example.hisync.repository.TaskRepository;
import com.example.hisync.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepo;
    private final SessionRepository sessionRepo;
    private final UserRepository userRepo;

    // Member: get my tasks
    @GetMapping
    @Transactional
    public ResponseEntity<List<TaskResponse>> getMyTasks(@RequestParam Long userId) {
        return ResponseEntity.ok(
                taskRepo.findByAssignedToId(userId)
                        .stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList())
        );
    }

    // Leader: get all tasks for a band grouped by session
    @GetMapping("/band/{bandId}")
    @Transactional
    public ResponseEntity<List<SessionTasksDto>> getTasksByBand(@PathVariable Long bandId) {
        List<Session> sessions = sessionRepo.findByBandId(bandId);
        List<SessionTasksDto> result = new ArrayList<>();

        for (Session session : sessions) {
            List<TaskResponse> tasks = taskRepo.findBySessionId(session.getId())
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());

            String songTitle = session.getLineup() != null
                    ? session.getLineup().getSongTitle() : "—";

            result.add(new SessionTasksDto(
                    session.getId(),
                    songTitle,
                    session.getDate(),
                    tasks
            ));
        }

        return ResponseEntity.ok(result);
    }

    // Leader: create task
    @PostMapping
    @Transactional
    public ResponseEntity<TaskResponse> createTask(@RequestBody Map<String, Object> body) {
        Long sessionId  = Long.valueOf(body.get("sessionId").toString());
        Long assignedTo = Long.valueOf(body.get("assignedTo").toString());
        String title    = (String) body.get("title");

        Session session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Session not found"));

        User user = userRepo.findById(assignedTo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        Task task = new Task();
        task.setSession(session);
        task.setAssignedTo(user);
        task.setTitle(title);
        task.setStatus(Task.Status.pending);

        return ResponseEntity.status(201).body(toResponse(taskRepo.save(task)));
    }

    // Leader: edit task title or reassign
    @PatchMapping("/{id}")
    @Transactional
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        Task task = taskRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"));

        if (body.containsKey("title")) {
            String title = (String) body.get("title");
            if (title != null && !title.isBlank()) task.setTitle(title);
        }

        if (body.containsKey("assignedTo")) {
            Long assignedTo = Long.valueOf(body.get("assignedTo").toString());
            User user = userRepo.findById(assignedTo)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "User not found"));
            task.setAssignedTo(user);
        }

        return ResponseEntity.ok(toResponse(taskRepo.save(task)));
    }

    // Leader: delete task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (!taskRepo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        taskRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Member: update status (leader approval flow)
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Task task = taskRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"));
        try {
            task.setStatus(Task.Status.valueOf(body.get("status")));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }
        return ResponseEntity.ok(toResponse(taskRepo.save(task)));
    }

    // Member: submit recording — auto sets status to submitted
    @PatchMapping("/{id}/recording")
    public ResponseEntity<TaskResponse> submitRecording(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Task task = taskRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"));
        task.setRecordingUrl(body.get("recordingUrl"));
        task.setStatus(Task.Status.submitted);
        return ResponseEntity.ok(toResponse(taskRepo.save(task)));
    }

    private TaskResponse toResponse(Task task) {
        String sessionSong = "";
        String sessionDate = "";
        if (task.getSession() != null) {
            if (task.getSession().getLineup() != null)
                sessionSong = task.getSession().getLineup().getSongTitle();
            if (task.getSession().getDate() != null)
                sessionDate = task.getSession().getDate().toString();
        }
        return new TaskResponse(
                task.getId(),
                task.getSession() != null ? task.getSession().getId() : null,
                task.getTitle(),
                task.getStatus().name(),
                task.getAssignedTo() != null
                        ? task.getAssignedTo().getDisplayName() : "Unknown",
                task.getRecordingUrl(),
                sessionSong,
                sessionDate
        );
    }

    //Get the submissions                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
    @GetMapping("/band/{bandId}/submissions")
    @Transactional
    public ResponseEntity<List<TaskResponse>> getSubmissions(@PathVariable Long bandId) {
        List<Session> sessions = sessionRepo.findByBandId(bandId);
        List<TaskResponse> result = new ArrayList<>();
        for (Session session : sessions) {
            taskRepo.findBySessionId(session.getId()).stream()
                    .filter(t -> t.getStatus() == Task.Status.submitted)
                    .map(this::toResponse)
                    .forEach(result::add);
        }
        return ResponseEntity.ok(result);
    }
}