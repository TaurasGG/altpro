package com.taurasg.altpro.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    private String id;

    @NotBlank(message = "organizationId is required")
    private String organizationId;

    @NotBlank(message = "name is required")
    private String name;

    private String description;
    private Instant createdAt;
    private List<String> members;
}
