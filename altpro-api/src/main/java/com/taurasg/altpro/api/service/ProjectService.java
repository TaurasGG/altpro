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
    public ProjectService(ProjectRepository repo) { this.repo = repo; }


    public Project create(Project p) {
        p.setCreatedAt(Instant.now());
        return repo.save(p);
    }
    public Project getById(String id) { return repo.findById(id).orElseThrow(() -> new NotFoundException("Project not found: " + id)); }
    public Project update(String id, Project p) {
        Project existing = getById(id);
        existing.setName(p.getName());
        existing.setDescription(p.getDescription());
        existing.setMembers(p.getMembers());
        return repo.save(existing);
    }
    public void delete(String id) { if(!repo.existsById(id)) throw new NotFoundException("Project not found: " + id); repo.deleteById(id); }
    public List<Project> listAll() { return repo.findAll(); }
}