package com.agilesprint.it;

import org.junit.jupiter.api.Test;

class AuthControllerIT extends ControllerITSupport {
    @Test
    void returnsUnauthenticatedStatusWithoutCredentials() {
        client.get().uri("/api/auth/status").exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.authenticated").isEqualTo(false);
    }

    @Test
    void returnsAuthenticatedStatusAndProfileForBasicUser() {
        authenticatedClient.get().uri("/api/auth/status").exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.authenticated").isEqualTo(true)
                .jsonPath("$.user.username").isEqualTo("alice");

        authenticatedClient.get().uri("/api/me").exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.subject").isEqualTo("alice")
                .jsonPath("$.username").isEqualTo("alice");
    }
}
