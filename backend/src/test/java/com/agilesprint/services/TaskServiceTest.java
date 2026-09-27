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
import com.agilesprint.domain.Board;
import com.agilesprint.domain.BoardColumn;
import com.agilesprint.domain.Task;
import com.agilesprint.domain.TaskPriority;
import com.agilesprint.repositories.BoardColumnRepository;
import com.agilesprint.repositories.TaskRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private BoardColumnRepository columnRepository;
    @Mock
    private BoardService boardService;
    @Mock
    private ActivityService activityService;

    private TaskService service;
    private final UserContext user = new UserContext("user-1", "alice", "alice@example.com", "Alice");

    @BeforeEach
    void setUp() {
        service = new TaskService(taskRepository, columnRepository, boardService, activityService);
    }

    @Test
    void createTrimsTitleAndUsesDefaults() {
        Board board = Board.builder().id("board-1").workspaceId("workspace-1").build();
        BoardColumn column = BoardColumn.builder().id("column-1").boardId("board-1").build();
        when(boardService.requireBoard("board-1", user)).thenReturn(Mono.just(board));
        when(columnRepository.findByBoardIdOrderByPositionAsc("board-1")).thenReturn(Flux.just(column));
        when(taskRepository.findByBoardIdOrderByColumnIdAscPositionAsc("board-1")).thenReturn(Flux.empty());
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId("task-1");
            return Mono.just(task);
        });
        when(activityService.record(anyString(), anyString(), any(UserContext.class), anyString(), anyString()))
                .thenReturn(Mono.just(Activity.builder().build()));

        StepVerifier.create(service.create("board-1",
                        new ApiDtos.TaskRequest("  Write tests  ", null, null, null, null, null), user))
                .assertNext(response -> {
                    assertEquals("Write tests", response.title());
                    assertEquals("", response.description());
                    assertEquals(TaskPriority.MEDIUM, response.priority());
                    assertEquals("column-1", response.columnId());
                    assertEquals(0, response.position());
                })
                .verifyComplete();

        verify(activityService).record("workspace-1", "task-1", user, "TASK_CREATED",
                "created task \"Write tests\"");
    }

    @Test
    void createRejectsBlankTitleBeforeCallingDependencies() {
        StepVerifier.create(service.create("board-1",
                        new ApiDtos.TaskRequest("  ", null, null, null, null, null), user))
                .expectErrorMessage("Task title is required")
                .verify();
    }

    @Test
    void createRejectsColumnFromAnotherBoard() {
        when(boardService.requireBoard("board-1", user))
                .thenReturn(Mono.just(Board.builder().id("board-1").workspaceId("workspace-1").build()));
        when(columnRepository.findById("foreign-column"))
                .thenReturn(Mono.just(BoardColumn.builder().id("foreign-column").boardId("another-board").build()));

        StepVerifier.create(service.create("board-1",
                        new ApiDtos.TaskRequest("Task", null, null, null, null, "foreign-column"), user))
                .expectErrorMessage("Column not found on this board")
                .verify();
    }
}
