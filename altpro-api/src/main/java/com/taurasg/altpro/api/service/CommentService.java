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

    // --- USER METHODS ---
    public Comment createForUser(Comment c, String email) {
        c.setId(null);
        c.setAuthor(email);
        c.setCreatedAt(Instant.now());
        return repo.save(c);
    }

    public Comment getByIdForUser(String id, String email) {
        Comment c = getById(id);
        if (!c.getAuthor().equals(email)) throw new NotFoundException("Comment not found: " + id);
        return c;
    }

    public Comment updateForUser(String id, Comment update, String email) {
        Comment ex = getById(id);
        if (!ex.getAuthor().equals(email)) throw new NotFoundException("Comment not found: " + id);
        ex.setText(update.getText());
        return repo.save(ex);
    }

    public void deleteForUser(String id, String email) {
        Comment ex = getById(id);
        if (!ex.getAuthor().equals(email)) throw new NotFoundException("Comment not found: " + id);
        repo.delete(ex);
    }

    public List<Comment> listAllForUser(String email) {
        return repo.findAll().stream().filter(c -> c.getAuthor().equals(email)).toList();
    }

    public List<Comment> listByTaskForUser(String taskId, String email) {
        return repo.findByTaskId(taskId).stream().filter(c -> c.getAuthor().equals(email)).toList();
    }

    // --- ADMIN METHODS ---
    public List<Comment> listAll() { return repo.findAll(); }

    public void adminDelete(String id) {
        if (!repo.existsById(id)) throw new NotFoundException("Comment not found: " + id);
        repo.deleteById(id);
    }

    public Comment adminUpdate(String id, Comment update) {
        Comment ex = getById(id);
        ex.setText(update.getText());
        ex.setAuthor(update.getAuthor()); // admin can reassign author if needed
        return repo.save(ex);
    }

    // Helper
    private Comment getById(String id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Comment not found: " + id));
    }
}