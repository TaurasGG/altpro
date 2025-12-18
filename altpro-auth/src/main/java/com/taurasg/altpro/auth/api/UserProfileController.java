package com.taurasg.altpro.auth.api;

import com.taurasg.altpro.auth.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

record UserProfile(String id, String email, String username, String displayName) {}

@RestController
@RequestMapping("/auth/users")
@CrossOrigin
public class UserProfileController {
    private final UserRepository users;
    public UserProfileController(UserRepository users) { this.users = users; }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<?> byUsername(@PathVariable String username) {
        var u = users.findAll().stream().filter(x -> username.equals(x.getUsername())).findFirst();
        return u.<ResponseEntity<?>>map(user -> ResponseEntity.ok(new UserProfile(user.getId(), user.getEmail(), user.getUsername(), user.getDisplayName())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<?> byEmail(@PathVariable String email) {
        var u = users.findByEmail(email);
        return u.<ResponseEntity<?>>map(user -> ResponseEntity.ok(new UserProfile(user.getId(), user.getEmail(), user.getUsername(), user.getDisplayName())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-id/{id}")
    public ResponseEntity<?> byId(@PathVariable String id) {
        var u = users.findById(id);
        return u.<ResponseEntity<?>>map(user -> ResponseEntity.ok(new UserProfile(user.getId(), user.getEmail(), user.getUsername(), user.getDisplayName())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
