
package com.smarttaskai.backend.controller;

import com.smarttaskai.backend.service.AiService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:5174"
})
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    public record TaskPrompt(
            @NotBlank(message = "Task description is required")
            @Size(max = 500, message = "Task description must not exceed 500 characters")
            String taskDescription
    ) {}

    
@PostMapping("/suggest")
public ResponseEntity<Map<String, String>> suggest(
        @Valid @RequestBody TaskPrompt request) {

    try {
        String suggestion =
                aiService.suggestTaskSteps(request.taskDescription());

        return ResponseEntity.ok(
                Map.of("suggestion", suggestion));

    } catch (ResourceAccessException ex) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error",
                        "AI service is unavailable. Please try again later."
                ));
    }
}

}
