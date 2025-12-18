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
@RequestMapping("/api/orgs/{orgId}/projects")
public class ProjectController {
    private final ProjectService service;
    public ProjectController(ProjectService service) { this.service = service; }

    // --- USER ENDPOINTS ---
    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Project> create(@PathVariable String orgId, @Valid @RequestBody Project p, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        p.setOrganizationId(orgId);
        Project saved = service.createForUser(p, email);
        return ResponseEntity.status(201).body(saved);
    }

    @PreAuthorize("hasAuthority('SCOPE_api.read')")
    @GetMapping("/{id}")
    public ResponseEntity<Project> getById(@PathVariable String orgId, @PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        return ResponseEntity.ok(service.getByIdForUser(id, email));
    }

    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @PutMapping("/{id}")
    public ResponseEntity<Project> update(@PathVariable String orgId, @PathVariable String id, @Valid @RequestBody Project p, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        return ResponseEntity.ok(service.updateForUser(id, p, email));
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
    public ResponseEntity<List<Project>> listAll(@PathVariable String orgId, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        return ResponseEntity.ok(service.listAllForUser(email));
    }
}
