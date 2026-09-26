package com.agilesprint.services;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;

public record UserContext(String id, String username, String email, String displayName) {
    public static UserContext from(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof OidcUser user) {
            String username = value(user.getClaims(), "preferred_username", user.getName());
            String email = value(user.getClaims(), "email", "");
            String name = value(user.getClaims(), "name", username);
            return new UserContext(user.getSubject(), username, email, name);
        }
        if (principal instanceof Jwt jwt) {
            String username = value(jwt.getClaims(), "preferred_username", jwt.getSubject());
            return new UserContext(jwt.getSubject(), username, value(jwt.getClaims(), "email", ""),
                    value(jwt.getClaims(), "name", username));
        }
        String name = authentication.getName();
        return new UserContext(name, name, "", name);
    }

    private static String value(Map<String, Object> claims, String key, String fallback) {
        Object value = claims.get(key);
        return value == null || value.toString().isBlank() ? fallback : value.toString();
    }
}
