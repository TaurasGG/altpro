package com.taurasg.altpro.api.controller;

import com.taurasg.altpro.api.model.OrgRole;
import com.taurasg.altpro.api.model.Organization;
import com.taurasg.altpro.api.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orgs")
public class OrganizationController {
    private final OrganizationService service;
    public OrganizationController(OrganizationService service) { this.service = service; }

    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Organization> create(@Valid @RequestBody Organization o, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.status(201).body(service.createForUser(o, userId));
    }

    @PreAuthorize("hasAuthority('SCOPE_api.read')")
    @GetMapping
    public ResponseEntity<List<Organization>> list(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(service.listAllForUser(userId));
    }

    @PreAuthorize("hasAuthority('SCOPE_api.read')")
    @GetMapping("/{id}")
    public ResponseEntity<Organization> get(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(service.getByIdForUser(id, userId));
    }

    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @PutMapping("/{id}")
    public ResponseEntity<Organization> update(@PathVariable String id, @Valid @RequestBody Organization o, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(service.updateForAdmin(id, o, userId));
    }

    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        service.deleteForAdmin(id, userId);
        return ResponseEntity.noContent().build();
    }

    // Member management
    record MemberRequest(String username, String email, OrgRole role) {}

    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @PostMapping("/{id}/members")
    public ResponseEntity<Organization> addMember(@PathVariable String id, @RequestBody MemberRequest req, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        String target = req.username() != null ? req.username() : req.email();
        var added = service.addMemberByIdentity(id, target, req.username() != null, req.role(), userId);
        return ResponseEntity.ok(added);
    }

    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @PutMapping("/{id}/members/{memberId}")
    public ResponseEntity<Organization> updateMember(@PathVariable String id, @PathVariable String memberId, @RequestBody MemberRequest req, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(service.updateMemberRole(id, memberId, req.role(), userId));
    }

    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @DeleteMapping("/{id}/members/{memberId}")
    public ResponseEntity<Organization> removeMember(@PathVariable String id, @PathVariable String memberId, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(service.removeMember(id, memberId, userId));
    }
}
