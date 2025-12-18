package com.taurasg.altpro.api.controller;

import com.taurasg.altpro.api.model.Task;
import com.taurasg.altpro.api.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orgs/{orgId}/tasks")
public class TaskController {
    private final TaskService service;
    public TaskController(TaskService service) { this.service = service; }

    // --- USER ENDPOINTS ---
    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Task> create(@PathVariable String orgId, @Valid @RequestBody Task t, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        Task saved = service.createForUser(t, email);
        return ResponseEntity.status(201).body(saved);
    }

    @PreAuthorize("hasAuthority('SCOPE_api.read')")
    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable String orgId, @PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        return ResponseEntity.ok(service.getByIdForUser(id, email));
    }

    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable String orgId, @PathVariable String id, @Valid @RequestBody Task t, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        return ResponseEntity.ok(service.updateForUser(id, t, email));
    }

    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String orgId, @PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        service.deleteForUser(id, email);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_api.read')")
    @GetMapping
    public ResponseEntity<List<Task>> listAll(@PathVariable String orgId, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        return ResponseEntity.ok(service.listAllForUser(email));
    }

    @PreAuthorize("hasAuthority('SCOPE_api.read')")
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Task>> listByProject(@PathVariable String orgId, @PathVariable String projectId, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        return ResponseEntity.ok(service.listByProjectForUser(projectId, email));
    }

    // admin endpoints removed; org-admin enforced in services
}
