package com.agilesprint.services;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.agilesprint.controllers.ApiDtos;
import com.agilesprint.domain.Board;
import com.agilesprint.domain.BoardColumn;
import com.agilesprint.domain.Task;
import com.agilesprint.domain.TaskPriority;
import com.agilesprint.mappers.ApiMapper;
import com.agilesprint.repositories.BoardColumnRepository;
import com.agilesprint.repositories.TaskRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final BoardColumnRepository columnRepository;
    private final BoardService boardService;
    private final ActivityService activityService;

    public TaskService(TaskRepository taskRepository, BoardColumnRepository columnRepository, BoardService boardService,
            ActivityService activityService) {
        this.taskRepository = taskRepository;
        this.columnRepository = columnRepository;
        this.boardService = boardService;
        this.activityService = activityService;
    }

    public Flux<ApiDtos.TaskResponse> list(String boardId, UserContext user) {
        return boardService.requireBoard(boardId, user)
                .thenMany(taskRepository.findByBoardIdOrderByColumnIdAscPositionAsc(boardId).map(ApiMapper::task));
    }

    public Mono<ApiDtos.TaskResponse> create(String boardId, ApiDtos.TaskRequest request, UserContext user) {
        if (request == null || request.title() == null || request.title().isBlank()) {
            return Mono.error(new IllegalArgumentException("Task title is required"));
        }
        return boardService.requireBoard(boardId, user).flatMap(board -> firstColumn(boardId, request.columnId())
                .flatMap(column -> taskRepository.findByBoardIdOrderByColumnIdAscPositionAsc(boardId)
                        .filter(task -> column.getId().equals(task.getColumnId())).count()
                        .flatMap(count -> {
                    int position = count.intValue();
                    Instant now = Instant.now();
                    Task task = Task.builder().workspaceId(board.getWorkspaceId()).boardId(board.getId())
                            .columnId(column.getId()).title(request.title().trim())
                            .description(request.description() == null ? "" : request.description())
                            .priority(request.priority() == null ? TaskPriority.MEDIUM : request.priority())
                            .assigneeId(request.assigneeId()).assigneeUsername(request.assigneeUsername())
                            .createdBy(user.id()).createdByUsername(user.displayName()).position(position)
                            .createdAt(now).updatedAt(now).build();
                    return taskRepository.save(task)
                            .flatMap(saved -> activityService.record(board.getWorkspaceId(), saved.getId(), user, "TASK_CREATED",
                                    "created task \"" + saved.getTitle() + "\"").thenReturn(saved));
                })).map(ApiMapper::task));
    }

    public Mono<ApiDtos.TaskResponse> update(String taskId, ApiDtos.TaskUpdateRequest request, UserContext user) {
        if (request == null) {
            return Mono.error(new IllegalArgumentException("Task update is required"));
        }
        return requireTask(taskId, user).flatMap(task -> {
            if (request.title() != null && !request.title().isBlank()) {
                task.setTitle(request.title().trim());
            }
            if (request.description() != null) task.setDescription(request.description());
            if (request.priority() != null) task.setPriority(request.priority());
            if (request.assigneeId() != null) task.setAssigneeId(request.assigneeId());
            if (request.assigneeUsername() != null) task.setAssigneeUsername(request.assigneeUsername());
            Mono<Task> saved = request.columnId() == null ? Mono.just(task) : moveColumn(task, request.columnId());
            task.setUpdatedAt(Instant.now());
            return saved.flatMap(taskRepository::save)
                    .flatMap(updated -> activityService.record(updated.getWorkspaceId(), updated.getId(), user, "TASK_UPDATED",
                            "updated task \"" + updated.getTitle() + "\"").thenReturn(updated));
        }).map(ApiMapper::task);
    }

    public Mono<ApiDtos.TaskResponse> move(String taskId, ApiDtos.MoveTaskRequest request, UserContext user) {
        if (request == null || request.columnId() == null || request.columnId().isBlank()) {
            return Mono.error(new IllegalArgumentException("A destination column is required"));
        }
        return requireTask(taskId, user).flatMap(task -> moveColumn(task, request.columnId())
                .flatMap(updated -> {
                    updated.setUpdatedAt(Instant.now());
                    return taskRepository.save(updated).flatMap(saved -> activityService.record(saved.getWorkspaceId(),
                            saved.getId(), user, "TASK_MOVED", "moved \"" + saved.getTitle() + "\"").thenReturn(saved));
                })).map(ApiMapper::task);
    }

    public Mono<Task> requireTask(String taskId, UserContext user) {
        return taskRepository.findById(taskId)
                .switchIfEmpty(Mono.error(new ServiceExceptions.NotFound("Task not found")))
                .flatMap(task -> boardService.requireBoard(task.getBoardId(), user).thenReturn(task));
    }

    private Mono<Task> moveColumn(Task task, String columnId) {
        return columnRepository.findById(columnId)
                .filter(column -> column.getBoardId().equals(task.getBoardId()))
                .switchIfEmpty(Mono.error(new ServiceExceptions.NotFound("Column not found on this board")))
                .flatMap(column -> taskRepository.findByBoardIdOrderByColumnIdAscPositionAsc(task.getBoardId())
                        .filter(existing -> columnId.equals(existing.getColumnId())).count()
                        .map(position -> {
                            task.setColumnId(column.getId());
                            task.setPosition(position.intValue());
                            return task;
                        }));
    }

    private Mono<BoardColumn> firstColumn(String boardId, String requestedColumnId) {
        if (requestedColumnId != null && !requestedColumnId.isBlank()) {
            return columnRepository.findById(requestedColumnId)
                    .filter(column -> column.getBoardId().equals(boardId))
                    .switchIfEmpty(Mono.error(new ServiceExceptions.NotFound("Column not found on this board")));
        }
        return columnRepository.findByBoardIdOrderByPositionAsc(boardId).next()
                .switchIfEmpty(Mono.error(new ServiceExceptions.NotFound("Board has no columns")));
    }
}
