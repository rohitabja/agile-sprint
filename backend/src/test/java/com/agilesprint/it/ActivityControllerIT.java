package com.agilesprint.it;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;

class ActivityControllerIT extends ControllerITSupport {
    @Test
    void listsWorkspaceActivity() {
        when(activityService.list("workspace-1", USER)).thenReturn(Flux.just(ACTIVITY_RESPONSE));

        authenticatedClient.get().uri("/api/workspaces/workspace-1/activity").exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$[0].type").isEqualTo("TASK_CREATED")
                .jsonPath("$[0].message").isEqualTo("created task");
    }
}
