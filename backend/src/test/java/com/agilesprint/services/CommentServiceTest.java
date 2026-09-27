package com.agilesprint.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agilesprint.controllers.ApiDtos;
import com.agilesprint.domain.Activity;
import com.agilesprint.domain.Task;
import com.agilesprint.domain.TaskComment;
import com.agilesprint.repositories.TaskCommentRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private TaskCommentRepository repository;
    @Mock
    private TaskService taskService;
    @Mock
    private ActivityService activityService;

    private CommentService service;
    private final UserContext user = new UserContext("user-1", "alice", "alice@example.com", "Alice");

    @BeforeEach
    void setUp() {
        service = new CommentService(repository, taskService, activityService);
    }

    @Test
    void addTrimsCommentAndRecordsActivity() {
        Task task = Task.builder().id("task-1").workspaceId("workspace-1").title("Release").build();
        when(taskService.requireTask("task-1", user)).thenReturn(Mono.just(task));
        when(repository.save(any(TaskComment.class))).thenAnswer(invocation -> {
            TaskComment comment = invocation.getArgument(0);
            comment.setId("comment-1");
            return Mono.just(comment);
        });
        when(activityService.record(anyString(), anyString(), any(UserContext.class), anyString(), anyString()))
                .thenReturn(Mono.just(Activity.builder().build()));

        StepVerifier.create(service.add("task-1", new ApiDtos.CommentRequest("  Looks good  "), user))
                .assertNext(response -> {
                    assertEquals("comment-1", response.id());
                    assertEquals("Looks good", response.body());
                    assertEquals("Alice", response.authorName());
                })
                .verifyComplete();

        verify(activityService).record("workspace-1", "task-1", user, "COMMENT_ADDED",
                "commented on \"Release\"");
    }

    @Test
    void addRejectsBlankComment() {
        StepVerifier.create(service.add("task-1", new ApiDtos.CommentRequest(" "), user))
                .expectErrorMessage("Comment text is required")
                .verify();
    }
}
