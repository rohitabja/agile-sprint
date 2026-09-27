package com.agilesprint.it;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.agilesprint.controllers.ApiDtos;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class WorkspaceControllerIT extends ControllerITSupport {
    @Test
    void listsAndCreatesWorkspaces() {
        when(workspaceService.list(USER)).thenReturn(Flux.just(WORKSPACE_RESPONSE));
        when(workspaceService.create(new ApiDtos.WorkspaceRequest("Sprint", "planning"), USER))
                .thenReturn(Mono.just(WORKSPACE_RESPONSE));

        authenticatedClient.get().uri("/api/workspaces").exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$[0].name").isEqualTo("Sprint");

        authenticatedClient.post().uri("/api/workspaces").contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"name":"Sprint","description":"planning"}
                        """).exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.id").isEqualTo("workspace-1");
    }

    @Test
    void listsAndInvitesWorkspaceMembers() {
        when(workspaceService.members("workspace-1", USER)).thenReturn(Flux.just(MEMBER_RESPONSE));
        when(workspaceService.invite("workspace-1",
                new ApiDtos.InviteRequest("bob", "bob@example.com"), USER))
                .thenReturn(Mono.just(MEMBER_RESPONSE));

        authenticatedClient.get().uri("/api/workspaces/workspace-1/members").exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$[0].username").isEqualTo("bob");

        authenticatedClient.post().uri("/api/workspaces/workspace-1/invites")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"username":"bob","email":"bob@example.com"}
                        """).exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.email").isEqualTo("bob@example.com");
    }

    @Test
    void mapsWorkspaceValidationErrorsToBadRequest() {
        when(workspaceService.create(new ApiDtos.WorkspaceRequest("", ""), USER))
                .thenReturn(Mono.error(new IllegalArgumentException("Workspace name is required")));

        authenticatedClient.post().uri("/api/workspaces").contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"name":"","description":""}
                        """).exchange()
                .expectStatus().isBadRequest()
                .expectBody().jsonPath("$.error").isEqualTo("Workspace name is required");
    }
}
