
package com.smarttaskai.backend.controller;

import com.smarttaskai.backend.dto.SubtaskResponse;
import com.smarttaskai.backend.entity.Subtask;
import com.smarttaskai.backend.service.SubtaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:5174"
})
public class SubtaskController {

    private final SubtaskService subtaskService;

    public SubtaskController(SubtaskService subtaskService) {
        this.subtaskService = subtaskService;
    }

    public record CreateSubtaskRequest(
            @NotBlank(message = "Subtask title is required")
            @Size(max = 200, message = "Subtask title must not exceed 200 characters")
            String title
    ) {}

    public record CompletionRequest(boolean completed) {}

    private SubtaskResponse toResponse(Subtask subtask) {
        return new SubtaskResponse(
                subtask.getId(),
                subtask.getTitle(),
                subtask.isCompleted(),
                subtask.getTask().getId()
        );
    }

    @GetMapping("/tasks/{taskId}/subtasks")
    public List<SubtaskResponse> getSubtasks(
            @PathVariable Long taskId) {
        return subtaskService.getSubtasks(taskId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping("/tasks/{taskId}/subtasks")
    @ResponseStatus(HttpStatus.CREATED)
    public SubtaskResponse createSubtask(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateSubtaskRequest request) {
        return toResponse(
                subtaskService.createSubtask(taskId, request.title())
        );
    }

    @PatchMapping("/subtasks/{subtaskId}")
    public SubtaskResponse updateCompletion(
            @PathVariable Long subtaskId,
            @RequestBody CompletionRequest request) {
        return toResponse(
                subtaskService.updateCompletion(
                        subtaskId, request.completed())
        );
    }

    @DeleteMapping("/subtasks/{subtaskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubtask(@PathVariable Long subtaskId) {
        subtaskService.deleteSubtask(subtaskId);
    }
}
