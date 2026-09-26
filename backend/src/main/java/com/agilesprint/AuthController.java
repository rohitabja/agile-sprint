package com.agilesprint;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agilesprint.services.UserContext;

@RestController
public class AuthController {
    @GetMapping("/api/auth/status")
    public Map<String, Object> status(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken || !authentication.isAuthenticated()) {
            return Map.of("authenticated", false);
        }
        UserContext user = UserContext.from(authentication);
        return Map.of("authenticated", true, "user", Map.of(
                "id", user.id(), "name", user.displayName(), "username", user.username(), "email", user.email()));
    }

    @GetMapping("/api/me")
    public Map<String, Object> profile(Authentication authentication) {
        UserContext user = UserContext.from(authentication);
        return Map.of("subject", user.id(), "username", user.username(), "email", user.email());
    }
}
