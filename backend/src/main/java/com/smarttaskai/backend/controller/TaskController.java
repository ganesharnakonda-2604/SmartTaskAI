
package com.smarttaskai.backend.controller;

import com.smarttaskai.backend.entity.Task;
import com.smarttaskai.backend.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:5174"
})
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@Valid @RequestBody Task task) {
    return taskService.createTask(task);
}

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Task not found"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
    
@PutMapping("/{id}")
public Task updateTask(
        @PathVariable Long id,
        @Valid @RequestBody Task task) {
    return taskService.updateTask(id, task);
}

}
