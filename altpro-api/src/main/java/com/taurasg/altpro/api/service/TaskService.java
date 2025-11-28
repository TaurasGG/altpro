package com.taurasg.altpro.api.service;

import com.taurasg.altpro.api.exception.NotFoundException;
import com.taurasg.altpro.api.model.Task;
import com.taurasg.altpro.api.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository repo;

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    // --- USER METHODS ---
    public Task createForUser(Task t, String email) {
        t.setId(null);
        t.setCreatedAt(Instant.now());
        t.setAssignee(email); // assign to creator by default
        return repo.save(t);
    }

    public Task getByIdForUser(String id, String email) {
        Task task = getById(id);
        if (!email.equals(task.getAssignee())) {
            throw new NotFoundException("Task not found: " + id);
        }
        return task;
    }

    public Task updateForUser(String id, Task update, String email) {
        Task existing = getById(id);
        if (!email.equals(existing.getAssignee())) {
            throw new NotFoundException("Task not found: " + id);
        }
        existing.setTitle(update.getTitle());
        existing.setDescription(update.getDescription());
        existing.setStatus(update.getStatus());
        existing.setPriority(update.getPriority());
        return repo.save(existing);
    }

    public void deleteForUser(String id, String email) {
        Task existing = getById(id);
        if (!email.equals(existing.getAssignee())) {
            throw new NotFoundException("Task not found: " + id);
        }
        repo.delete(existing);
    }

    public List<Task> listAllForUser(String email) {
        return repo.findAll().stream()
                .filter(t -> email.equals(t.getAssignee()))
                .toList();
    }

    public List<Task> listByProjectForUser(String projectId, String email) {
        return repo.findByProjectId(projectId).stream()
                .filter(t -> email.equals(t.getAssignee()))
                .toList();
    }

    // --- ADMIN METHODS ---
    public Task create(Task t) {
        t.setCreatedAt(Instant.now());
        return repo.save(t);
    }

    public Task getById(String id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Task not found: " + id));
    }

    public Task update(String id, Task t) {
        Task existing = getById(id);
        existing.setTitle(t.getTitle());
        existing.setDescription(t.getDescription());
        existing.setStatus(t.getStatus());
        existing.setPriority(t.getPriority());
        existing.setAssignee(t.getAssignee());
        return repo.save(existing);
    }

    public void delete(String id) {
        if (!repo.existsById(id)) throw new NotFoundException("Task not found: " + id);
        repo.deleteById(id);
    }

    public List<Task> listAll() {
        return repo.findAll();
    }

    public List<Task> listByProject(String projectId) {
        return repo.findByProjectId(projectId);
    }
}