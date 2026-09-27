package com.agilesprint.it;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.agilesprint.controllers.ApiDtos;
import com.agilesprint.domain.Board;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class BoardControllerIT extends ControllerITSupport {
    @Test
    void listsCreatesAndGetsBoards() {
        Board board = Board.builder().id("board-1").workspaceId("workspace-1").name("Main board").build();
        when(boardService.list("workspace-1", USER)).thenReturn(Flux.just(BOARD_RESPONSE));
        when(boardService.create("workspace-1", new ApiDtos.BoardRequest("Main board"), USER))
                .thenReturn(Mono.just(BOARD_RESPONSE));
        when(boardService.requireBoard("board-1", USER)).thenReturn(Mono.just(board));
        when(boardService.response(board)).thenReturn(Mono.just(BOARD_RESPONSE));

        authenticatedClient.get().uri("/api/workspaces/workspace-1/boards").exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$[0].name").isEqualTo("Main board");

        authenticatedClient.post().uri("/api/workspaces/workspace-1/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"name":"Main board"}
                        """).exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.columns[0].key").isEqualTo("todo");

        authenticatedClient.get().uri("/api/boards/board-1").exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.id").isEqualTo("board-1");
    }
}
