
package com.smarttaskai.backend.service;

import com.smarttaskai.backend.entity.Subtask;
import com.smarttaskai.backend.entity.Task;
import com.smarttaskai.backend.repository.SubtaskRepository;
import com.smarttaskai.backend.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SubtaskService {

    private final SubtaskRepository subtaskRepository;
    private final TaskRepository taskRepository;

    public SubtaskService(
            SubtaskRepository subtaskRepository,
            TaskRepository taskRepository) {
        this.subtaskRepository = subtaskRepository;
        this.taskRepository = taskRepository;
    }

    public List<Subtask> getSubtasks(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Task not found");
        }

        return subtaskRepository.findByTaskId(taskId);
    }

    @Transactional
    public Subtask createSubtask(Long taskId, String title) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"));

        Subtask subtask = new Subtask();
        subtask.setTitle(title.trim());
        subtask.setTask(task);

        return subtaskRepository.save(subtask);
    }

    @Transactional
    public Subtask updateCompletion(Long subtaskId, boolean completed) {
        Subtask subtask = subtaskRepository.findById(subtaskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Subtask not found"));

        subtask.setCompleted(completed);
        return subtaskRepository.save(subtask);
    }

    @Transactional
    public void deleteSubtask(Long subtaskId) {
        if (!subtaskRepository.existsById(subtaskId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Subtask not found");
        }

        subtaskRepository.deleteById(subtaskId);
    }
}
