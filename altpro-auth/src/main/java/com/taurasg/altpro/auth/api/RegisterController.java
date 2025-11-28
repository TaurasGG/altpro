package com.taurasg.altpro.auth.api;

import com.taurasg.altpro.auth.user.User;
import com.taurasg.altpro.auth.user.UserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

record RegisterRequest(@Email String email, @NotBlank String password) {}

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class RegisterController {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public RegisterController(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (users.existsByEmail(req.email())) {
            return ResponseEntity.badRequest().body("{\"error\":\"Email already registered\"}");
        }
        var u = new User();
        u.setEmail(req.email());
        u.setPasswordHash(encoder.encode(req.password()));
        u.setRoles(Set.of("USER"));
        users.save(u);
        return ResponseEntity.ok().build();
    }
}