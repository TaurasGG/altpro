package com.taurasg.altpro.api.service;

import com.taurasg.altpro.api.exception.NotFoundException;
import com.taurasg.altpro.api.model.Invitation;
import com.taurasg.altpro.api.model.Organization;
import com.taurasg.altpro.api.repository.InvitationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class InvitationService {
    private final InvitationRepository repo;
    private final OrganizationService orgs;
    private final AuthClient auth;

    public InvitationService(InvitationRepository repo, OrganizationService orgs, AuthClient auth) {
        this.repo = repo;
        this.orgs = orgs;
        this.auth = auth;
    }

    public Invitation inviteByUsername(String orgId, String inviterUserId, String username) {
        Organization org = orgs.getById(orgId);
        if (!orgs.isAdmin(orgId, inviterUserId)) throw new NotFoundException("Organization not found: " + orgId);
        Invitation inv = new Invitation();
        inv.setId(null);
        inv.setOrganizationId(orgId);
        inv.setInviterUserId(inviterUserId);
        inv.setInviteeUsername(username);
        inv.setStatus("PENDING");
        inv.setCreatedAt(Instant.now());
        return repo.save(inv);
    }

    public Invitation inviteByEmail(String orgId, String inviterUserId, String email) {
        Organization org = orgs.getById(orgId);
        if (!orgs.isAdmin(orgId, inviterUserId)) throw new NotFoundException("Organization not found: " + orgId);
        Invitation inv = new Invitation();
        inv.setId(null);
        inv.setOrganizationId(orgId);
        inv.setInviterUserId(inviterUserId);
        inv.setInviteeEmail(email);
        inv.setStatus("PENDING");
        inv.setCreatedAt(Instant.now());
        return repo.save(inv);
    }

    public List<Invitation> listForUser(String userId, String email, String username) {
        return repo.findAll().stream()
                .filter(i -> "PENDING".equals(i.getStatus()))
                .filter(i -> (i.getInviteeEmail() != null && i.getInviteeEmail().equals(email)) ||
                             (i.getInviteeUsername() != null && i.getInviteeUsername().equals(username)))
                .toList();
    }

    public Invitation accept(String id, String userId) {
        Invitation inv = repo.findById(id).orElseThrow(() -> new NotFoundException("Invitation not found: " + id));
        Organization org = orgs.getById(inv.getOrganizationId());
        if (!"PENDING".equals(inv.getStatus())) return inv;
        orgs.addMember(org.getId(), userId, com.taurasg.altpro.api.model.OrgRole.MEMBER, userId);
        inv.setStatus("ACCEPTED");
        return repo.save(inv);
    }

    public Invitation decline(String id, String userId) {
        Invitation inv = repo.findById(id).orElseThrow(() -> new NotFoundException("Invitation not found: " + id));
        if (!"PENDING".equals(inv.getStatus())) return inv;
        inv.setStatus("DECLINED");
        return repo.save(inv);
    }
}

