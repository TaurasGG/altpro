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
    public ProjectService(ProjectRepository repo, OrganizationService organizations) {
        this.repo = repo;
        this.organizations = organizations;
    }

    // --- USER METHODS ---
    public Project createForUser(Project p, String email) {
        p.setId(null);
        p.setCreatedAt(Instant.now());
        if (!organizations.isAdmin(p.getOrganizationId(), email)) {
            throw new NotFoundException("Organization not found: " + p.getOrganizationId());
        }
        if (p.getMembers() == null || p.getMembers().isEmpty()) {
            p.setMembers(List.of(email));
        } else if (!p.getMembers().contains(email)) {
            p.getMembers().add(email);
        }
        return repo.save(p);
    }

    public Project getByIdForUser(String id, String email) {
        Project project = getById(id);
        boolean isMember = project.getMembers() != null && project.getMembers().contains(email);
        boolean isOrgAdmin = organizations.isAdmin(project.getOrganizationId(), email);
        if (!isMember && !isOrgAdmin) {
            throw new NotFoundException("Project not found: " + id);
        }
        return project;
    }

    public Project updateForUser(String id, Project p, String email) {
        Project existing = getById(id);
        boolean isMember = existing.getMembers() != null && existing.getMembers().contains(email);
        boolean isOrgAdmin = organizations.isAdmin(existing.getOrganizationId(), email);
        if (!isMember && !isOrgAdmin) {
            throw new NotFoundException("Project not found: " + id);
        }
        existing.setName(p.getName());
        existing.setDescription(p.getDescription());
        existing.setMembers(p.getMembers());
        return repo.save(existing);
    }

    public void deleteForUser(String id, String email) {
        Project existing = getById(id);
        if (!organizations.isAdmin(existing.getOrganizationId(), email)) {
            throw new NotFoundException("Project not found: " + id);
        }
        repo.delete(existing);
    }

    public List<Project> listAllForUser(String email) {
        return repo.findAll().stream()
                .filter(pr -> (pr.getMembers() != null && pr.getMembers().contains(email)) || organizations.isAdmin(pr.getOrganizationId(), email))
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

    public void delete(String id) {
        if (!repo.existsById(id)) throw new NotFoundException("Project not found: " + id);
        repo.deleteById(id);
    }

    public List<Project> listAll() {
        return repo.findAll();
    }
}
