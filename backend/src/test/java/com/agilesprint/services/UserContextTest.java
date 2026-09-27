package com.agilesprint.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

class UserContextTest {
    @Test
    void mapsJwtClaimsAndUsesSubjectFallbacks() {
        Jwt jwt = new Jwt("token", Instant.EPOCH, Instant.MAX, Map.of("alg", "none"),
                Map.of("sub", "user-1", "preferred_username", "alice", "email", "alice@example.com", "name", "Alice"));

        UserContext result = UserContext.from(new TestingAuthenticationToken(jwt, "credentials"));

        assertEquals("user-1", result.id());
        assertEquals("alice", result.username());
        assertEquals("alice@example.com", result.email());
        assertEquals("Alice", result.displayName());
    }

    @Test
    void mapsGenericAuthenticationNameToAllIdentityFields() {
        UserContext result = UserContext.from(new TestingAuthenticationToken("bob", "credentials"));

        assertEquals(new UserContext("bob", "bob", "", "bob"), result);
    }
}
