package com.agilesprint.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.agilesprint.services.TaskService;
import com.agilesprint.services.UserContext;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping("/boards/{boardId}/tasks")
    public Flux<ApiDtos.TaskResponse> list(@PathVariable("boardId") String boardId, Authentication authentication) {
        return service.list(boardId, UserContext.from(authentication));
    }

    @PostMapping("/boards/{boardId}/tasks")
    public Mono<ApiDtos.TaskResponse> create(@PathVariable("boardId") String boardId,
            @RequestBody ApiDtos.TaskRequest request,
            Authentication authentication) {
        return service.create(boardId, request, UserContext.from(authentication));
    }

    @PatchMapping("/tasks/{taskId}")
    public Mono<ApiDtos.TaskResponse> update(@PathVariable("taskId") String taskId,
            @RequestBody ApiDtos.TaskUpdateRequest request, Authentication authentication) {
        return service.update(taskId, request, UserContext.from(authentication));
    }

    @PostMapping("/tasks/{taskId}/move")
    public Mono<ApiDtos.TaskResponse> move(@PathVariable("taskId") String taskId,
            @RequestBody ApiDtos.MoveTaskRequest request,
            Authentication authentication) {
        return service.move(taskId, request, UserContext.from(authentication));
    }
}
