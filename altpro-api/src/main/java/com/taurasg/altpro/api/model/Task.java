package com.taurasg.altpro.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    private String id;

    @NotBlank(message = "projectId is required")
    private String projectId;

    @NotBlank(message = "title is required")
    private String title;

    private String description;

    @NotNull
    private TaskStatus status;

    private int priority;
    private Instant createdAt;
    private String assignee;
}