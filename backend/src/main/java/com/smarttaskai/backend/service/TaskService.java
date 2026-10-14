
package com.smarttaskai.backend.service;

import com.smarttaskai.backend.entity.Task;
import com.smarttaskai.backend.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
    
public Task updateTask(Long id, Task updatedTask) {
    Task existingTask = taskRepository.findById(id)
            .orElseThrow(() ->
                    new org.springframework.web.server.ResponseStatusException(
                            org.springframework.http.HttpStatus.NOT_FOUND,
                            "Task not found"));

    existingTask.setTitle(updatedTask.getTitle());
    existingTask.setDescription(updatedTask.getDescription());
    existingTask.setPriority(updatedTask.getPriority());
    existingTask.setCompleted(updatedTask.isCompleted());

    return taskRepository.save(existingTask);
}

}
