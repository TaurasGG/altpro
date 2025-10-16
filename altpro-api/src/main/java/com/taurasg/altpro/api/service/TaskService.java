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
    public TaskService(TaskRepository repo) { this.repo = repo; }

    public Task create(Task t) { t.setCreatedAt(Instant.now()); return repo.save(t); }
    public Task getById(String id) { return repo.findById(id).orElseThrow(() -> new NotFoundException("Task not found: " + id)); }
    public Task update(String id, Task t) {
        Task existing = getById(id);
        existing.setTitle(t.getTitle());
        existing.setDescription(t.getDescription());
        existing.setStatus(t.getStatus());
        existing.setPriority(t.getPriority());
        existing.setAssignee(t.getAssignee());
        return repo.save(existing);
    }
    public void delete(String id) { if(!repo.existsById(id)) throw new NotFoundException("Task not found: " + id); repo.deleteById(id); }
    public List<Task> listAll() { return repo.findAll(); }
    public List<Task> listByProject(String projectId) { return repo.findByProjectId(projectId); }
}