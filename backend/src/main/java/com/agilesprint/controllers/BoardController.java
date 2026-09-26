package com.agilesprint.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.agilesprint.services.BoardService;
import com.agilesprint.services.UserContext;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class BoardController {
    private final BoardService service;

    public BoardController(BoardService service) {
        this.service = service;
    }

    @GetMapping("/workspaces/{workspaceId}/boards")
    public Flux<ApiDtos.BoardResponse> list(@PathVariable("workspaceId") String workspaceId,
            Authentication authentication) {
        return service.list(workspaceId, UserContext.from(authentication));
    }

    @PostMapping("/workspaces/{workspaceId}/boards")
    public Mono<ApiDtos.BoardResponse> create(@PathVariable("workspaceId") String workspaceId,
            @RequestBody ApiDtos.BoardRequest request, Authentication authentication) {
        return service.create(workspaceId, request, UserContext.from(authentication));
    }

    @GetMapping("/boards/{boardId}")
    public Mono<ApiDtos.BoardResponse> get(@PathVariable("boardId") String boardId, Authentication authentication) {
        return service.requireBoard(boardId, UserContext.from(authentication)).flatMap(service::response);
    }
}
