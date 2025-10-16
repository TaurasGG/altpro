package com.taurasg.altpro.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    private String id;

    @NotBlank(message = "taskId is required")
    private String taskId;

    private String author;

    @NotBlank(message = "text is required")
    private String text;

    private Instant createdAt;
}