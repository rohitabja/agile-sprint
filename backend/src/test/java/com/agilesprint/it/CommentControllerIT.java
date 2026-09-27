package com.agilesprint.it;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.agilesprint.controllers.ApiDtos;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class CommentControllerIT extends ControllerITSupport {
    @Test
    void listsAndAddsComments() {
        when(commentService.list("task-1", USER)).thenReturn(Flux.just(COMMENT_RESPONSE));
        when(commentService.add("task-1", new ApiDtos.CommentRequest("Looks good"), USER))
                .thenReturn(Mono.just(COMMENT_RESPONSE));

        authenticatedClient.get().uri("/api/tasks/task-1/comments").exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$[0].body").isEqualTo("Looks good");

        authenticatedClient.post().uri("/api/tasks/task-1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"body":"Looks good"}
                        """).exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.authorName").isEqualTo("alice");
    }
}
