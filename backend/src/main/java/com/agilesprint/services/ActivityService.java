package com.agilesprint.services;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.agilesprint.controllers.ApiDtos;
import com.agilesprint.domain.Activity;
import com.agilesprint.mappers.ApiMapper;
import com.agilesprint.repositories.ActivityRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ActivityService {
    private final ActivityRepository repository;
    private final WorkspaceService workspaceService;

    public ActivityService(ActivityRepository repository, WorkspaceService workspaceService) {
        this.repository = repository;
        this.workspaceService = workspaceService;
    }

    public Mono<Activity> record(String workspaceId, String taskId, UserContext user, String type, String message) {
        return repository.save(Activity.builder().workspaceId(workspaceId).taskId(taskId).actorId(user.id())
                .actorName(user.displayName()).type(type).message(message).createdAt(Instant.now()).build());
    }

    public Flux<ApiDtos.ActivityResponse> list(String workspaceId, UserContext user) {
        return workspaceService.requireWorkspace(workspaceId, user).thenMany(
                repository.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId).map(ApiMapper::activity));
    }
}
