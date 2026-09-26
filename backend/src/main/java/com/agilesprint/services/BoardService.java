package com.agilesprint.services;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.agilesprint.controllers.ApiDtos;
import com.agilesprint.domain.Board;
import com.agilesprint.domain.BoardColumn;
import com.agilesprint.mappers.ApiMapper;
import com.agilesprint.repositories.BoardColumnRepository;
import com.agilesprint.repositories.BoardRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardColumnRepository columnRepository;
    private final WorkspaceService workspaceService;

    public BoardService(BoardRepository boardRepository, BoardColumnRepository columnRepository,
            WorkspaceService workspaceService) {
        this.boardRepository = boardRepository;
        this.columnRepository = columnRepository;
        this.workspaceService = workspaceService;
    }

    public Flux<ApiDtos.BoardResponse> list(String workspaceId, UserContext user) {
        return workspaceService.requireWorkspace(workspaceId, user)
                .thenMany(boardRepository.findByWorkspaceIdOrderByCreatedAtAsc(workspaceId)
                        .flatMap(this::response));
    }

    public Mono<ApiDtos.BoardResponse> create(String workspaceId, ApiDtos.BoardRequest request, UserContext user) {
        if (request == null || request.name() == null || request.name().isBlank()) {
            return Mono.error(new IllegalArgumentException("Board name is required"));
        }
        return workspaceService.requireAdmin(workspaceId, user)
                .then(boardRepository.save(Board.builder().workspaceId(workspaceId).name(request.name().trim())
                        .createdAt(Instant.now()).build()))
                .flatMap(board -> columnRepository.saveAll(defaultColumns(board.getId())).then(response(board)));
    }

    public Mono<Board> requireBoard(String boardId, UserContext user) {
        return boardRepository.findById(boardId)
                .switchIfEmpty(Mono.error(new ServiceExceptions.NotFound("Board not found")))
                .flatMap(board -> workspaceService.requireWorkspace(board.getWorkspaceId(), user).thenReturn(board));
    }

    public Mono<ApiDtos.BoardResponse> response(Board board) {
        return columnRepository.findByBoardIdOrderByPositionAsc(board.getId()).collectList()
                .map(columns -> ApiMapper.board(board, columns));
    }

    private List<BoardColumn> defaultColumns(String boardId) {
        return List.of(
                BoardColumn.builder().boardId(boardId).name("To Do").key("todo").position(0).build(),
                BoardColumn.builder().boardId(boardId).name("In Progress").key("in-progress").position(1).build(),
                BoardColumn.builder().boardId(boardId).name("Testing").key("testing").position(2).build(),
                BoardColumn.builder().boardId(boardId).name("Done").key("done").position(3).build());
    }
}
