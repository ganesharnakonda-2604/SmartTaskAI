
package com.smarttaskai.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class AiService {

    private final RestClient restClient =
            RestClient.create("http://localhost:11434");

    public String suggestTaskSteps(String taskDescription) {

        String prompt = """
                You are a helpful task planning assistant.
                Break the following task into 3 to 5 simple,
                practical steps for a beginner.

                Task: %s
                """.formatted(taskDescription);

        Map<?, ?> response = restClient.post()
                .uri("/api/generate")
                .body(Map.of(
                        "model", "llama3.2:3b",
                        "prompt", prompt,
                        "stream", false
                ))
                .retrieve()
                .body(Map.class);

        if (response == null || response.get("response") == null) {
            throw new IllegalStateException(
                    "Ollama returned an empty response");
        }

        return response.get("response").toString();
    }
}
