package com.agilesprint.it;

import org.junit.jupiter.api.Test;

class ApiHelloControllerIT extends ControllerITSupport {
    @Test
    void returnsHelloMessage() {
        client.get().uri("/api/hello").exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.message")
                .isEqualTo("This protected response came from AgileSprint.");
    }
}
