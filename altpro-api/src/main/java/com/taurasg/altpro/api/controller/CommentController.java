package com.taurasg.altpro.api.controller;

import com.taurasg.altpro.api.model.Comment;
import com.taurasg.altpro.api.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService service;
    public CommentController(CommentService service) { this.service = service; }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Comment> create(@Valid @RequestBody Comment c) { return ResponseEntity.status(201).body(service.create(c)); }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Comment> getById(@PathVariable String id) { return ResponseEntity.ok(service.getById(id)); }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Comment> update(@PathVariable String id, @Valid @RequestBody Comment c) { return ResponseEntity.ok(service.update(id, c)); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) { service.delete(id); return ResponseEntity.noContent().build(); }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Comment>> listAll() { return ResponseEntity.ok(service.listAll()); }

    // Hierarchinis: comments by task id
    @GetMapping(value = "/task/{taskId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Comment>> listByTask(@PathVariable String taskId) { return ResponseEntity.ok(service.listByTask(taskId)); }
}