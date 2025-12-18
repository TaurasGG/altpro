package com.taurasg.altpro.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AuthClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;

    public AuthClient(@Value("${auth.base-url:http://localhost:9000}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Map<?,?> findByUsername(String username) {
        return restTemplate.getForObject(baseUrl + "/auth/users/by-username/" + username, Map.class);
    }

    public Map<?,?> findByEmail(String email) {
        return restTemplate.getForObject(baseUrl + "/auth/users/by-email/" + email, Map.class);
    }
}

