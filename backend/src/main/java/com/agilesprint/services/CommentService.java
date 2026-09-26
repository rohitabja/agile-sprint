package com.agilesprint.services;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.agilesprint.controllers.ApiDtos;
import com.agilesprint.domain.TaskComment;
import com.agilesprint.mappers.ApiMapper;
import com.agilesprint.repositories.TaskCommentRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CommentService {
    private final TaskCommentRepository repository;
    private final TaskService taskService;
    private final ActivityService activityService;

    public CommentService(TaskCommentRepository repository, TaskService taskService, ActivityService activityService) {
        this.repository = repository;
        this.taskService = taskService;
        this.activityService = activityService;
    }

    public Flux<ApiDtos.CommentResponse> list(String taskId, UserContext user) {
        return taskService.requireTask(taskId, user)
                .thenMany(repository.findByTaskIdOrderByCreatedAtAsc(taskId).map(ApiMapper::comment));
    }

    public Mono<ApiDtos.CommentResponse> add(String taskId, ApiDtos.CommentRequest request, UserContext user) {
        if (request == null || request.body() == null || request.body().isBlank()) {
            return Mono.error(new IllegalArgumentException("Comment text is required"));
        }
        return taskService.requireTask(taskId, user).flatMap(task -> repository.save(TaskComment.builder()
                .taskId(taskId).workspaceId(task.getWorkspaceId()).authorId(user.id()).authorName(user.displayName())
                .body(request.body().trim()).createdAt(Instant.now()).build())
                .flatMap(comment -> activityService.record(task.getWorkspaceId(), taskId, user, "COMMENT_ADDED",
                        "commented on \"" + task.getTitle() + "\"").thenReturn(comment)))
                .map(ApiMapper::comment);
    }
}
