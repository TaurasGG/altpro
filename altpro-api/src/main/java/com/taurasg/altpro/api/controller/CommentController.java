package com.taurasg.altpro.api.controller;

import com.taurasg.altpro.api.model.Comment;
import com.taurasg.altpro.api.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orgs/{orgId}/comments")
public class CommentController {
    private final CommentService service;
    public CommentController(CommentService service) { this.service = service; }

    // --- USER ENDPOINTS ---
    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Comment> create(@PathVariable String orgId, @Valid @RequestBody Comment c, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        return ResponseEntity.status(201).body(service.createForUser(c, email));
    }

    @PreAuthorize("hasAuthority('SCOPE_api.read')")
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getById(@PathVariable String orgId, @PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        return ResponseEntity.ok(service.getByIdForUser(id, email));
    }

    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @PutMapping("/{id}")
    public ResponseEntity<Comment> update(@PathVariable String orgId, @PathVariable String id, @Valid @RequestBody Comment c, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        return ResponseEntity.ok(service.updateForUser(id, c, email));
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
    public ResponseEntity<List<Comment>> listAll(@PathVariable String orgId, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        return ResponseEntity.ok(service.listAllForUser(email));
    }

    @PreAuthorize("hasAuthority('SCOPE_api.read')")
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Comment>> listByTask(@PathVariable String orgId, @PathVariable String taskId, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        return ResponseEntity.ok(service.listByTaskForUser(taskId, email));
    }
}
