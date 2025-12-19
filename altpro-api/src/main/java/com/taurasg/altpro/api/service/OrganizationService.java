package com.taurasg.altpro.api.service;

import com.taurasg.altpro.api.exception.NotFoundException;
import com.taurasg.altpro.api.model.OrgMember;
import com.taurasg.altpro.api.model.OrgRole;
import com.taurasg.altpro.api.model.Organization;
import com.taurasg.altpro.api.repository.OrganizationRepository;
import com.taurasg.altpro.api.repository.InvitationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrganizationService {
    private final OrganizationRepository repo;
    private final AuthClient auth;
    private final com.taurasg.altpro.api.repository.ProjectRepository projects;
    private final com.taurasg.altpro.api.repository.TaskRepository tasks;
    private final com.taurasg.altpro.api.repository.CommentRepository comments;
    private final InvitationRepository invitations;

    public OrganizationService(OrganizationRepository repo, AuthClient auth,
                               com.taurasg.altpro.api.repository.ProjectRepository projects,
                               com.taurasg.altpro.api.repository.TaskRepository tasks,
                               com.taurasg.altpro.api.repository.CommentRepository comments,
                               InvitationRepository invitations) {
        this.repo = repo; this.auth = auth; this.projects = projects; this.tasks = tasks; this.comments = comments; this.invitations = invitations;
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
        var orgInvites = invitations.findByOrganizationId(id);
        invitations.deleteAll(orgInvites);
        var orgProjects = projects.findByOrganizationId(id);
        for (var p : orgProjects) {
            var projTasks = tasks.findByProjectId(p.getId());
            for (var t : projTasks) {
                var tComments = comments.findByTaskId(t.getId());
                comments.deleteAll(tComments);
            }
            tasks.deleteAll(projTasks);
        }
        projects.deleteAll(orgProjects);
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
        var target = members.stream().filter(m -> m.getUserId().equals(targetUserId)).findFirst().orElse(null);
        if (target == null) return org;
        if (target.getRole() == OrgRole.ADMIN) throw new NotFoundException("Organization not found: " + id);
        members.removeIf(m -> m.getUserId().equals(targetUserId));
        org.setMembers(members);
        return repo.save(org);
    }

    public Organization addMemberByIdentity(String id, String identity, boolean isUsername, OrgRole role, String userId) {
        var org = getById(id);
        requireAdmin(org, userId);
        String targetUserId;
        var profile = isUsername ? auth.findByUsername(identity) : auth.findByEmail(identity);
        if (profile == null || profile.get("id") == null) throw new NotFoundException("User not found: " + identity);
        targetUserId = (String) profile.get("id");
        return addMember(id, targetUserId, role, userId);
    }

    public Organization addMemberByInvitation(String id, String targetUserId) {
        var org = getById(id);
        var members = org.getMembers() == null ? new ArrayList<OrgMember>() : new ArrayList<>(org.getMembers());
        if (members.stream().noneMatch(m -> m.getUserId().equals(targetUserId))) {
            members.add(new OrgMember(targetUserId, OrgRole.MEMBER));
        }
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

    public boolean exists(String id) {
        return repo.existsById(id);
    }

    private void requireAdmin(Organization org, String userId) {
        var isAdmin = org.getMembers() != null &&
                org.getMembers().stream().anyMatch(m -> m.getUserId().equals(userId) && m.getRole() == OrgRole.ADMIN);
        if (!isAdmin) throw new NotFoundException("Organization not found: " + org.getId());
    }
}
