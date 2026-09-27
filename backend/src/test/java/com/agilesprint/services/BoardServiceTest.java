package com.agilesprint.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agilesprint.controllers.ApiDtos;
import com.agilesprint.domain.Board;
import com.agilesprint.domain.BoardColumn;
import com.agilesprint.domain.Workspace;
import com.agilesprint.domain.WorkspaceMember;
import com.agilesprint.repositories.BoardColumnRepository;
import com.agilesprint.repositories.BoardRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private BoardColumnRepository columnRepository;
    @Mock
    private WorkspaceService workspaceService;

    private BoardService service;
    private final UserContext user = new UserContext("user-1", "alice", "alice@example.com", "Alice");

    @BeforeEach
    void setUp() {
        service = new BoardService(boardRepository, columnRepository, workspaceService);
    }

    @Test
    void createTrimsNameAndCreatesDefaultColumns() {
        when(workspaceService.requireAdmin("workspace-1", user))
                .thenReturn(Mono.just(WorkspaceMember.builder().build()));
        when(boardRepository.save(any(Board.class))).thenAnswer(invocation -> {
            Board board = invocation.getArgument(0);
            board.setId("board-1");
            return Mono.just(board);
        });
        when(columnRepository.saveAll(org.mockito.ArgumentMatchers.<Iterable<BoardColumn>>any()))
                .thenReturn(Flux.empty());
        when(columnRepository.findByBoardIdOrderByPositionAsc("board-1")).thenReturn(Flux.just(
                BoardColumn.builder().id("col-1").name("To Do").key("todo").position(0).build(),
                BoardColumn.builder().id("col-2").name("In Progress").key("in-progress").position(1).build(),
                BoardColumn.builder().id("col-3").name("Testing").key("testing").position(2).build(),
                BoardColumn.builder().id("col-4").name("Done").key("done").position(3).build()));

        StepVerifier.create(service.create("workspace-1", new ApiDtos.BoardRequest("  Planning  "), user))
                .assertNext(response -> {
                    assertEquals("Planning", response.name());
                    assertEquals(4, response.columns().size());
                    assertEquals("todo", response.columns().getFirst().key());
                })
                .verifyComplete();
    }

    @Test
    void requireBoardChecksWorkspaceMembership() {
        Board board = Board.builder().id("board-1").workspaceId("workspace-1").build();
        when(boardRepository.findById("board-1")).thenReturn(Mono.just(board));
        when(workspaceService.requireWorkspace("workspace-1", user))
                .thenReturn(Mono.just(Workspace.builder().id("workspace-1").build()));

        StepVerifier.create(service.requireBoard("board-1", user))
                .expectNext(board)
                .verifyComplete();
    }
}
