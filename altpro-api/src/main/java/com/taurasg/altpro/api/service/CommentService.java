package com.taurasg.altpro.api.service;

import com.taurasg.altpro.api.exception.NotFoundException;
import com.taurasg.altpro.api.model.Comment;
import com.taurasg.altpro.api.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class CommentService {
    private final CommentRepository repo;
    public CommentService(CommentRepository repo) { this.repo = repo; }

    public Comment create(Comment c) { c.setCreatedAt(Instant.now()); return repo.save(c); }
    public Comment getById(String id) { return repo.findById(id).orElseThrow(() -> new NotFoundException("Comment not found: " + id)); }
    public Comment update(String id, Comment c) {
        Comment ex = getById(id);
        ex.setText(c.getText());
        ex.setAuthor(c.getAuthor());
        return repo.save(ex);
    }
    public void delete(String id) { if(!repo.existsById(id)) throw new NotFoundException("Comment not found: " + id); repo.deleteById(id); }
    public List<Comment> listAll() { return repo.findAll(); }
    public List<Comment> listByTask(String taskId) { return repo.findByTaskId(taskId); }
}