package com.agilesprint.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.agilesprint.services.CommentService;
import com.agilesprint.services.UserContext;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
public class CommentController {
    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<ApiDtos.CommentResponse> list(@PathVariable("taskId") String taskId, Authentication authentication) {
        return service.list(taskId, UserContext.from(authentication));
    }

    @PostMapping
    public Mono<ApiDtos.CommentResponse> add(@PathVariable("taskId") String taskId,
            @RequestBody ApiDtos.CommentRequest request,
            Authentication authentication) {
        return service.add(taskId, request, UserContext.from(authentication));
    }
}
