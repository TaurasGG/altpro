package com.taurasg.altpro.api.controller;

import com.taurasg.altpro.api.model.Task;
import com.taurasg.altpro.api.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService service;
    public TaskController(TaskService service) { this.service = service; }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Task> create(@Valid @RequestBody Task t) {
        Task saved = service.create(t);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Task> getById(@PathVariable String id) { return ResponseEntity.ok(service.getById(id)); }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Task> update(@PathVariable String id, @Valid @RequestBody Task t) { return ResponseEntity.ok(service.update(id, t)); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) { service.delete(id); return ResponseEntity.ok().build(); }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Task>> listAll() { return ResponseEntity.ok(service.listAll()); }

    // Hierarchinis endpoint'as: tasks by project
    @GetMapping(value = "/project/{projectId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Task>> listByProject(@PathVariable String projectId) { return ResponseEntity.ok(service.listByProject(projectId)); }
}