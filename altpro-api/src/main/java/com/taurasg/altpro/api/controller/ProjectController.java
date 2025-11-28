package com.taurasg.altpro.api.controller;

import com.taurasg.altpro.api.model.Project;
import com.taurasg.altpro.api.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService service;
    public ProjectController(ProjectService service) { this.service = service; }

    // --- USER ENDPOINTS ---
    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Project> create(@Valid @RequestBody Project p, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        Project saved = service.createForUser(p, email);
        return ResponseEntity.status(201).body(saved);
    }

    @PreAuthorize("hasAuthority('SCOPE_api.read')")
    @GetMapping("/{id}")
    public ResponseEntity<Project> getById(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        return ResponseEntity.ok(service.getByIdForUser(id, email));
    }

    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @PutMapping("/{id}")
    public ResponseEntity<Project> update(@PathVariable String id, @Valid @RequestBody Project p, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        return ResponseEntity.ok(service.updateForUser(id, p, email));
    }

    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        service.deleteForUser(id, email);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_api.read')")
    @GetMapping
    public ResponseEntity<List<Project>> listAll(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        return ResponseEntity.ok(service.listAllForUser(email));
    }

    // --- ADMIN ENDPOINTS ---
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<List<Project>> adminListAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> adminDelete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<Project> adminUpdate(@PathVariable String id, @Valid @RequestBody Project p) {
        return ResponseEntity.ok(service.update(id, p));
    }
}