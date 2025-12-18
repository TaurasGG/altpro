package com.taurasg.altpro.api.service;

import com.taurasg.altpro.api.exception.NotFoundException;
import com.taurasg.altpro.api.model.Task;
import com.taurasg.altpro.api.repository.ProjectRepository;
import com.taurasg.altpro.api.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepo;
    private final ProjectRepository projectRepo; // add
    private final OrganizationService organizations;

    public TaskService(TaskRepository taskRepo, ProjectRepository projectRepo, OrganizationService organizations) {
        this.taskRepo = taskRepo;
        this.projectRepo = projectRepo;
        this.organizations = organizations;
    }

    // Create: assignee = creator
    public Task createForUser(Task t, String email) {
        t.setId(null);
        t.setCreatedAt(Instant.now());
        var project = projectRepo.findById(t.getProjectId())
                .orElseThrow(() -> new NotFoundException("Project not found: " + t.getProjectId()));
        var members = project.getMembers();
        boolean isMember = members != null && members.contains(email);
        boolean isOrgAdmin = organizations.isAdmin(project.getOrganizationId(), email);
        if (!isMember && !isOrgAdmin) {
            throw new NotFoundException("Project not found: " + t.getProjectId());
        }
        t.setAssignee(email);
        return taskRepo.save(t);
    }

    // Read: allow if user is member of the task's project
    public Task getByIdForUser(String id, String userId) {
        Task task = getById(id);
        var project = projectRepo.findById(task.getProjectId())
                .orElseThrow(() -> new NotFoundException("Project not found: " + task.getProjectId()));
        boolean isMember = project.getMembers() != null && project.getMembers().contains(userId);
        boolean isOrgAdmin = organizations.isAdmin(project.getOrganizationId(), userId);
        if (!isMember && !isOrgAdmin) {
            throw new NotFoundException("Task not found: " + id);
        }
        return task;
    }

    // Update: only assignee
    public Task updateForUser(String id, Task update, String email) {
        Task existing = getById(id);
        var project = projectRepo.findById(existing.getProjectId())
                .orElseThrow(() -> new NotFoundException("Project not found: " + existing.getProjectId()));
        boolean isOrgAdmin = organizations.isAdmin(project.getOrganizationId(), email);
        if (!email.equals(existing.getAssignee()) && !isOrgAdmin) {
            throw new NotFoundException("Task not found: " + id);
        }
        existing.setTitle(update.getTitle());
        existing.setDescription(update.getDescription());
        existing.setStatus(update.getStatus());
        existing.setPriority(update.getPriority());
        return taskRepo.save(existing);
    }

    // Delete: only assignee
    public void deleteForUser(String id, String email) {
        Task existing = getById(id);
        var project = projectRepo.findById(existing.getProjectId())
                .orElseThrow(() -> new NotFoundException("Project not found: " + existing.getProjectId()));
        boolean isOrgAdmin = organizations.isAdmin(project.getOrganizationId(), email);
        if (!email.equals(existing.getAssignee()) && !isOrgAdmin) {
            throw new NotFoundException("Task not found: " + id);
        }
        taskRepo.delete(existing);
    }

    // List all: tasks where user is member of their projects (filter by project membership)
    public List<Task> listAllForUser(String userId) {
        return taskRepo.findAll().stream()
                .filter(t -> {
                    var project = projectRepo.findById(t.getProjectId()).orElse(null);
                    if (project == null) return false;
                    boolean isMember = project.getMembers() != null && project.getMembers().contains(userId);
                    boolean isOrgAdmin = organizations.isAdmin(project.getOrganizationId(), userId);
                    return isMember || isOrgAdmin;
                })
                .toList();
    }

    // List by project: any member of the project can view tasks
    public List<Task> listByProjectForUser(String projectId, String userId) {
        var project = projectRepo.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found: " + projectId));

        boolean isMember = project.getMembers() != null && project.getMembers().contains(userId);
        boolean isOrgAdmin = organizations.isAdmin(project.getOrganizationId(), userId);
        if (!isMember && !isOrgAdmin) {
            throw new NotFoundException("Not a member of project: " + projectId);
        }

        return taskRepo.findByProjectId(projectId); // grąžina visas užduotis projekto nariams
    }

    // Admin and helpers
    public List<Task> listAll() {
        return taskRepo.findAll();
    }

    public Task update(String id, Task t) {
        Task existing = getById(id);
        existing.setProjectId(t.getProjectId());
        existing.setTitle(t.getTitle());
        existing.setDescription(t.getDescription());
        existing.setStatus(t.getStatus());
        existing.setPriority(t.getPriority());
        existing.setAssignee(t.getAssignee());
        // keep original createdAt
        return taskRepo.save(existing);
    }

    public void delete(String id) {
        if (!taskRepo.existsById(id)) throw new NotFoundException("Task not found: " + id);
        taskRepo.deleteById(id);
    }

    public Task getById(String id) {
        return taskRepo.findById(id).orElseThrow(() -> new NotFoundException("Task not found: " + id));
    }

    // admin methods retained for compatibility
}
