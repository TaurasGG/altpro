package com.taurasg.altpro.api.service;

import com.taurasg.altpro.api.exception.NotFoundException;
import com.taurasg.altpro.api.model.OrgMember;
import com.taurasg.altpro.api.model.OrgRole;
import com.taurasg.altpro.api.model.Organization;
import com.taurasg.altpro.api.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrganizationService {
    private final OrganizationRepository repo;

    public OrganizationService(OrganizationRepository repo) {
        this.repo = repo;
    }

    public Organization createForUser(Organization o, String userId) {
        o.setId(null);
        o.setCreatedAt(Instant.now());
        var members = o.getMembers() == null ? new ArrayList<OrgMember>() : new ArrayList<>(o.getMembers());
        if (members.stream().noneMatch(m -> m.getUserId().equals(userId))) {
            members.add(new OrgMember(userId, OrgRole.ADMIN));
        }
        o.setMembers(members);
        return repo.save(o);
    }

    public List<Organization> listAllForUser(String userId) {
        return repo.findAll().stream()
                .filter(o -> o.getMembers() != null && o.getMembers().stream().anyMatch(m -> m.getUserId().equals(userId)))
                .toList();
    }

    public Organization getByIdForUser(String id, String userId) {
        var org = getById(id);
        if (org.getMembers() == null || org.getMembers().stream().noneMatch(m -> m.getUserId().equals(userId))) {
            throw new NotFoundException("Organization not found: " + id);
        }
        return org;
    }

    public Organization updateForAdmin(String id, Organization update, String userId) {
        var org = getById(id);
        requireAdmin(org, userId);
        org.setName(update.getName());
        org.setDescription(update.getDescription());
        return repo.save(org);
    }

    public void deleteForAdmin(String id, String userId) {
        var org = getById(id);
        requireAdmin(org, userId);
        repo.delete(org);
    }

    public Organization addMember(String id, String targetUserId, OrgRole role, String userId) {
        var org = getById(id);
        requireAdmin(org, userId);
        var members = org.getMembers() == null ? new ArrayList<OrgMember>() : new ArrayList<>(org.getMembers());
        if (members.stream().noneMatch(m -> m.getUserId().equals(targetUserId))) {
            members.add(new OrgMember(targetUserId, role));
        }
        org.setMembers(members);
        return repo.save(org);
    }

    public Organization updateMemberRole(String id, String targetUserId, OrgRole role, String userId) {
        var org = getById(id);
        requireAdmin(org, userId);
        var members = org.getMembers() == null ? new ArrayList<OrgMember>() : new ArrayList<>(org.getMembers());
        for (var m : members) {
            if (m.getUserId().equals(targetUserId)) {
                m.setRole(role);
                break;
            }
        }
        org.setMembers(members);
        return repo.save(org);
    }

    public Organization removeMember(String id, String targetUserId, String userId) {
        var org = getById(id);
        requireAdmin(org, userId);
        var members = org.getMembers() == null ? new ArrayList<OrgMember>() : new ArrayList<>(org.getMembers());
        members.removeIf(m -> m.getUserId().equals(targetUserId));
        org.setMembers(members);
        return repo.save(org);
    }

    public boolean isAdmin(String orgId, String userId) {
        var org = getById(orgId);
        return org.getMembers() != null &&
                org.getMembers().stream().anyMatch(m -> m.getUserId().equals(userId) && m.getRole() == OrgRole.ADMIN);
    }

    public boolean isMember(String orgId, String userId) {
        var org = getById(orgId);
        return org.getMembers() != null &&
                org.getMembers().stream().anyMatch(m -> m.getUserId().equals(userId));
    }

    public Organization getById(String id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Organization not found: " + id));
    }

    private void requireAdmin(Organization org, String userId) {
        var isAdmin = org.getMembers() != null &&
                org.getMembers().stream().anyMatch(m -> m.getUserId().equals(userId) && m.getRole() == OrgRole.ADMIN);
        if (!isAdmin) throw new NotFoundException("Organization not found: " + org.getId());
    }
}
