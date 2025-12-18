package com.taurasg.altpro.api.service;

import com.taurasg.altpro.api.exception.NotFoundException;
import com.taurasg.altpro.api.model.Project;
import com.taurasg.altpro.api.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository repo;
    private final OrganizationService organizations;
    private final AuthClient auth;
    public ProjectService(ProjectRepository repo, OrganizationService organizations, AuthClient auth) {
        this.repo = repo;
        this.organizations = organizations;
        this.auth = auth;
    }

    // --- USER METHODS ---
    public Project createForUser(Project p, String userId) {
        p.setId(null);
        p.setCreatedAt(Instant.now());
        if (!organizations.isAdmin(p.getOrganizationId(), userId)) {
            throw new NotFoundException("Organization not found: " + p.getOrganizationId());
        }
        if (p.getMembers() == null || p.getMembers().isEmpty()) {
            p.setMembers(List.of(userId));
        } else if (!p.getMembers().contains(userId)) {
            p.getMembers().add(userId);
        }
        return repo.save(p);
    }

    public Project getByIdForUser(String id, String userId) {
        Project project = getById(id);
        boolean isMember = project.getMembers() != null && project.getMembers().contains(userId);
        boolean isOrgAdmin = organizations.isAdmin(project.getOrganizationId(), userId);
        if (!isMember && !isOrgAdmin) {
            throw new NotFoundException("Project not found: " + id);
        }
        return project;
    }

    public Project updateForUser(String id, Project p, String userId) {
        Project existing = getById(id);
        boolean isMember = existing.getMembers() != null && existing.getMembers().contains(userId);
        boolean isOrgAdmin = organizations.isAdmin(existing.getOrganizationId(), userId);
        if (!isMember && !isOrgAdmin) {
            throw new NotFoundException("Project not found: " + id);
        }
        existing.setName(p.getName());
        existing.setDescription(p.getDescription());
        existing.setMembers(p.getMembers());
        return repo.save(existing);
    }

    public void deleteForUser(String id, String userId) {
        Project existing = getById(id);
        if (!organizations.isAdmin(existing.getOrganizationId(), userId)) {
            throw new NotFoundException("Project not found: " + id);
        }
        repo.delete(existing);
    }

    public List<Project> listAllForUser(String userId) {
        return repo.findAll().stream()
                .filter(pr -> (pr.getMembers() != null && pr.getMembers().contains(userId)) || organizations.isAdmin(pr.getOrganizationId(), userId))
                .toList();
    }

    // --- ADMIN METHODS ---
    public Project create(Project p) {
        p.setCreatedAt(Instant.now());
        return repo.save(p);
    }

    public Project getById(String id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Project not found: " + id));
    }

    public Project update(String id, Project p) {
        Project existing = getById(id);
        existing.setName(p.getName());
        existing.setDescription(p.getDescription());
        existing.setMembers(p.getMembers());
        return repo.save(existing);
    }

    public Project addMember(String projectId, String targetUserId, String userId) {
        var project = getById(projectId);
        if (!organizations.isAdmin(project.getOrganizationId(), userId)) {
            throw new NotFoundException("Project not found: " + projectId);
        }
        var members = project.getMembers() == null ? new java.util.ArrayList<String>() : new java.util.ArrayList<>(project.getMembers());
        if (members.stream().noneMatch(m -> m.equals(targetUserId))) {
            members.add(targetUserId);
        }
        project.setMembers(members);
        return repo.save(project);
    }

    public Project removeMember(String projectId, String targetUserId, String userId) {
        var project = getById(projectId);
        if (!organizations.isAdmin(project.getOrganizationId(), userId)) {
            throw new NotFoundException("Project not found: " + projectId);
        }
        var members = project.getMembers() == null ? new java.util.ArrayList<String>() : new java.util.ArrayList<>(project.getMembers());
        members.removeIf(m -> m.equals(targetUserId));
        project.setMembers(members);
        return repo.save(project);
    }

    public Project addMemberByIdentity(String projectId, String identity, boolean isUsername, String userId) {
        var profile = isUsername ? auth.findByUsername(identity) : auth.findByEmail(identity);
        if (profile == null || profile.get("id") == null) throw new NotFoundException("User not found: " + identity);
        String targetUserId = (String) profile.get("id");
        return addMember(projectId, targetUserId, userId);
    }

    public void delete(String id) {
        if (!repo.existsById(id)) throw new NotFoundException("Project not found: " + id);
        repo.deleteById(id);
    }

    public List<Project> listAll() {
        return repo.findAll();
    }
}
