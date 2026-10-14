
package com.smarttaskai.backend.dto;

public record SubtaskResponse(
        Long id,
        String title,
        boolean completed,
        Long taskId
) {}
