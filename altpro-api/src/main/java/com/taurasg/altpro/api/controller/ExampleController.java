package com.taurasg.altpro.api.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ExampleController {

    @GetMapping("/boards")
    public String boards(@AuthenticationPrincipal Jwt jwt, Authentication auth) {
        String subject = jwt.getSubject(); // email or user id depending on auth server
        return "User " + subject + " can read boards";
    }
}