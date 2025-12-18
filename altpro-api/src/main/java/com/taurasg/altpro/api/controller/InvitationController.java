package com.taurasg.altpro.api.controller;

import com.taurasg.altpro.api.model.Invitation;
import com.taurasg.altpro.api.service.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

record InviteRequest(String username, String email) {}

@RestController
@RequestMapping("/api")
public class InvitationController {
    private final InvitationService service;
    public InvitationController(InvitationService service) { this.service = service; }

    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @PostMapping("/orgs/{orgId}/invitations")
    public ResponseEntity<Invitation> invite(@PathVariable String orgId, @RequestBody InviteRequest req, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        if (req.username() != null && !req.username().isBlank()) {
            return ResponseEntity.status(201).body(service.inviteByUsername(orgId, userId, req.username()));
        }
        return ResponseEntity.status(201).body(service.inviteByEmail(orgId, userId, req.email()));
    }

    @PreAuthorize("hasAuthority('SCOPE_api.read')")
    @GetMapping("/invitations/mine")
    public ResponseEntity<List<Invitation>> mine(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String username = jwt.getClaimAsString("username");
        return ResponseEntity.ok(service.listForUser(userId, email, username));
    }

    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @PostMapping("/invitations/{id}/accept")
    public ResponseEntity<Void> accept(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        service.accept(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @PostMapping("/invitations/{id}/decline")
    public ResponseEntity<Void> decline(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        service.decline(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    @DeleteMapping("/orgs/{orgId}/invitations/{id}")
    public ResponseEntity<Void> cancel(@PathVariable String orgId, @PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        service.cancel(orgId, id, userId);
        return ResponseEntity.noContent().build();
    }
}

