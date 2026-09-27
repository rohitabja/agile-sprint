package com.agilesprint.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agilesprint.domain.Activity;
import com.agilesprint.domain.Workspace;
import com.agilesprint.repositories.ActivityRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {
    @Mock
    private ActivityRepository repository;
    @Mock
    private WorkspaceService workspaceService;

    private ActivityService service;
    private final UserContext user = new UserContext("user-1", "alice", "alice@example.com", "Alice");

    @BeforeEach
    void setUp() {
        service = new ActivityService(repository, workspaceService);
    }

    @Test
    void recordPersistsActorAndEventDetails() {
        when(repository.save(any(Activity.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.record("workspace-1", "task-1", user, "TASK_CREATED", "created a task"))
                .assertNext(activity -> {
                    assertEquals("workspace-1", activity.getWorkspaceId());
                    assertEquals("task-1", activity.getTaskId());
                    assertEquals("user-1", activity.getActorId());
                    assertEquals("Alice", activity.getActorName());
                    assertEquals("TASK_CREATED", activity.getType());
                    assertEquals("created a task", activity.getMessage());
                })
                .verifyComplete();

        verify(repository).save(any(Activity.class));
    }

    @Test
    void listRequiresWorkspaceAccess() {
        when(workspaceService.requireWorkspace("workspace-1", user))
                .thenReturn(Mono.just(Workspace.builder().id("workspace-1").build()));
        when(repository.findByWorkspaceIdOrderByCreatedAtDesc("workspace-1")).thenReturn(Flux.empty());

        StepVerifier.create(service.list("workspace-1", user)).verifyComplete();

        verify(workspaceService).requireWorkspace("workspace-1", user);
    }
}
