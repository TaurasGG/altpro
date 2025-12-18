package com.taurasg.altpro.api.service;

import com.taurasg.altpro.api.exception.NotFoundException;
import com.taurasg.altpro.api.model.Comment;
import com.taurasg.altpro.api.repository.CommentRepository;
import com.taurasg.altpro.api.repository.TaskRepository;
import com.taurasg.altpro.api.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class CommentService {
    private final CommentRepository repo;
    private final TaskRepository tasks;
    private final ProjectRepository projects;
    private final OrganizationService organizations;
    public CommentService(CommentRepository repo, TaskRepository tasks, ProjectRepository projects, OrganizationService organizations) {
        this.repo = repo;
        this.tasks = tasks;
        this.projects = projects;
        this.organizations = organizations;
    }

    // --- USER METHODS ---
    public Comment createForUser(Comment c, String email) {
        c.setId(null);
        c.setAuthor(email);
        c.setCreatedAt(Instant.now());
        var t = tasks.findById(c.getTaskId()).orElseThrow(() -> new NotFoundException("Task not found: " + c.getTaskId()));
        var p = projects.findById(t.getProjectId()).orElseThrow(() -> new NotFoundException("Project not found: " + t.getProjectId()));
        boolean isMember = p.getMembers() != null && p.getMembers().contains(email);
        boolean isOrgAdmin = organizations.isAdmin(p.getOrganizationId(), email);
        if (!isMember && !isOrgAdmin) throw new NotFoundException("Task not found: " + c.getTaskId());
        return repo.save(c);
    }

    public Comment getByIdForUser(String id, String email) {
        Comment c = getById(id);
        var t = tasks.findById(c.getTaskId()).orElseThrow(() -> new NotFoundException("Task not found: " + c.getTaskId()));
        var p = projects.findById(t.getProjectId()).orElseThrow(() -> new NotFoundException("Project not found: " + t.getProjectId()));
        boolean isMember = p.getMembers() != null && p.getMembers().contains(email);
        boolean isOrgAdmin = organizations.isAdmin(p.getOrganizationId(), email);
        if (!isMember && !isOrgAdmin && !c.getAuthor().equals(email)) throw new NotFoundException("Comment not found: " + id);
        return c;
    }

    public Comment updateForUser(String id, Comment update, String email) {
        Comment ex = getById(id);
        var t = tasks.findById(ex.getTaskId()).orElseThrow(() -> new NotFoundException("Task not found: " + ex.getTaskId()));
        var p = projects.findById(t.getProjectId()).orElseThrow(() -> new NotFoundException("Project not found: " + t.getProjectId()));
        boolean isOrgAdmin = organizations.isAdmin(p.getOrganizationId(), email);
        if (!ex.getAuthor().equals(email) && !isOrgAdmin) throw new NotFoundException("Comment not found: " + id);
        ex.setText(update.getText());
        return repo.save(ex);
    }

    public void deleteForUser(String id, String email) {
        Comment ex = getById(id);
        var t = tasks.findById(ex.getTaskId()).orElseThrow(() -> new NotFoundException("Task not found: " + ex.getTaskId()));
        var p = projects.findById(t.getProjectId()).orElseThrow(() -> new NotFoundException("Project not found: " + t.getProjectId()));
        boolean isOrgAdmin = organizations.isAdmin(p.getOrganizationId(), email);
        if (!ex.getAuthor().equals(email) && !isOrgAdmin) throw new NotFoundException("Comment not found: " + id);
        repo.delete(ex);
    }

    public List<Comment> listAllForUser(String email) {
        return repo.findAll().stream().filter(c -> {
            var t = tasks.findById(c.getTaskId()).orElse(null);
            if (t == null) return false;
            var p = projects.findById(t.getProjectId()).orElse(null);
            if (p == null) return false;
            boolean isMember = p.getMembers() != null && p.getMembers().contains(email);
            boolean isOrgAdmin = organizations.isAdmin(p.getOrganizationId(), email);
            return isMember || isOrgAdmin || c.getAuthor().equals(email);
        }).toList();
    }

    public List<Comment> listByTaskForUser(String taskId, String email) {
        var t = tasks.findById(taskId).orElseThrow(() -> new NotFoundException("Task not found: " + taskId));
        var p = projects.findById(t.getProjectId()).orElseThrow(() -> new NotFoundException("Project not found: " + t.getProjectId()));
        boolean isMember = p.getMembers() != null && p.getMembers().contains(email);
        boolean isOrgAdmin = organizations.isAdmin(p.getOrganizationId(), email);
        if (!isMember && !isOrgAdmin) throw new NotFoundException("Task not found: " + taskId);
        return repo.findByTaskId(taskId);
    }

    // --- ADMIN METHODS removed; org-admin enforced in user methods ---

    // Helper
    private Comment getById(String id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Comment not found: " + id));
    }
}
