
package com.smarttaskai.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarttaskai.backend.entity.Task;
import com.smarttaskai.backend.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.smarttaskai.backend.entity.Subtask;
import com.smarttaskai.backend.repository.SubtaskRepository;

import com.smarttaskai.backend.service.AiService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.ResourceAccessException;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BackendApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
private SubtaskRepository subtaskRepository;

@MockBean
private AiService aiService;


    
@BeforeEach
void cleanTestDatabase() {
    subtaskRepository.deleteAll();
    taskRepository.deleteAll();
}


    @Test
    void createValidTaskReturnsCreated() throws Exception {
        Task task = new Task();
        task.setTitle("Learn Spring Boot");
        task.setDescription("Practice REST APIs");
        task.setPriority("HIGH");

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Learn Spring Boot"));
    }

    @Test
    void getTasksReturnsOk() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk());
    }

    @Test
    void blankTaskTitleReturnsBadRequest() throws Exception {
        String invalidTask = """
                {
                  "title": "",
                  "description": "Invalid task",
                  "priority": "HIGH"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidTask))
                .andExpect(status().isBadRequest());
    }
    
@Test
void updateTaskReturnsOk() throws Exception {
    Task task = new Task();
    task.setTitle("Original task");
    task.setDescription("Original description");
    task.setPriority("MEDIUM");

    String response = mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(task)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Task createdTask = objectMapper.readValue(response, Task.class);

    createdTask.setTitle("Updated task");

    mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
            .put("/api/tasks/" + createdTask.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createdTask)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated task"));
}

@Test
void deleteTaskReturnsNoContent() throws Exception {
    Task task = new Task();
    task.setTitle("Task to delete");
    task.setDescription("Delete test");
    task.setPriority("LOW");

    String response = mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(task)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Task createdTask = objectMapper.readValue(response, Task.class);

    mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
            .delete("/api/tasks/" + createdTask.getId()))
            .andExpect(status().isNoContent());
}
@Test
void getSubtasksForTaskReturnsOk() throws Exception {
    Task task = new Task();
    task.setTitle("Learn Java");
    task.setDescription("Practice Java concepts");
    task.setPriority("HIGH");

    Task savedTask = taskRepository.save(task);

    Subtask subtask = new Subtask();
    subtask.setTitle("Learn collections");
    subtask.setCompleted(false);
    subtask.setTask(savedTask);

    subtaskRepository.save(subtask);

    mockMvc.perform(get("/api/tasks/" + savedTask.getId() + "/subtasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Learn collections"))
            .andExpect(jsonPath("$[0].completed").value(false));
}

@Test
void completeSubtaskReturnsUpdatedStatus() throws Exception {
    Task task = new Task();
    task.setTitle("Learn Java");
    task.setDescription("Practice Java concepts");
    task.setPriority("HIGH");

    Task savedTask = taskRepository.save(task);

    Subtask subtask = new Subtask();
    subtask.setTitle("Learn collections");
    subtask.setCompleted(false);
    subtask.setTask(savedTask);

    Subtask savedSubtask = subtaskRepository.save(subtask);

    mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .patch("/api/subtasks/" + savedSubtask.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"completed\":true}")
    )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.completed").value(true));
}

@Test
void deleteSubtaskReturnsNoContent() throws Exception {
    Task task = new Task();
    task.setTitle("Learn Java");
    task.setDescription("Practice Java concepts");
    task.setPriority("HIGH");

    Task savedTask = taskRepository.save(task);

    Subtask subtask = new Subtask();
    subtask.setTitle("Practice collections");
    subtask.setCompleted(false);
    subtask.setTask(savedTask);

    Subtask savedSubtask = subtaskRepository.save(subtask);

    mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .delete("/api/subtasks/" + savedSubtask.getId())
    )
            .andExpect(status().isNoContent());

    org.junit.jupiter.api.Assertions.assertFalse(
            subtaskRepository.existsById(savedSubtask.getId())
    );
}

@Test
void validAiPromptReturnsSuggestion() throws Exception {
    when(aiService.suggestTaskSteps(anyString()))
        .thenReturn("1. Break the task into smaller steps.");
    mockMvc.perform(post("/api/ai/suggest")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                      "taskDescription": "Learn Java collections"
                    }
                    """))
            .andExpect(status().isOk());
}

@Test
void blankAiPromptReturnsBadRequest() throws Exception {
    mockMvc.perform(post("/api/ai/suggest")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                      "taskDescription": ""
                    }
                    """))
            .andExpect(status().isBadRequest());
}

@Test
void aiServiceFailureDoesNotReturnSuccess() throws Exception {
    when(aiService.suggestTaskSteps(anyString()))
            .thenThrow(new ResourceAccessException(
                    "Ollama is unavailable"));

    mockMvc.perform(post("/api/ai/suggest")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                      "taskDescription": "Learn Java"
                    }
                    """))
            .andExpect(status().isServiceUnavailable())
.andExpect(jsonPath("$.error")
        .value("AI service is unavailable. Please try again later."));
}

}
