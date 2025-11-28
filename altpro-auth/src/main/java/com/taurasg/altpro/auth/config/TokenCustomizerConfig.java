package com.taurasg.altpro.auth.config;

import com.taurasg.altpro.auth.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

@Configuration
public class TokenCustomizerConfig {

    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer(UserRepository users) {
        return context -> {
            if (!"access_token".equals(context.getTokenType().getValue())) return;
            if (context.getAuthorizationGrantType().getValue().equals("client_credentials")) return;

            String email = context.getPrincipal().getName();
            var userOpt = users.findByEmail(email);
            if (userOpt.isEmpty()) return;

            var roles = userOpt.get().getRoles();
            context.getClaims().claim("roles", roles); // įrašome roles į token
        };
    }
}
