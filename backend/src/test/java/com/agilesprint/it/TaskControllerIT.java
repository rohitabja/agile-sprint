package com.agilesprint.it;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.agilesprint.controllers.ApiDtos;
import com.agilesprint.domain.TaskPriority;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class TaskControllerIT extends ControllerITSupport {
    @Test
    void listsCreatesUpdatesAndMovesTasks() {
        when(taskService.list("board-1", USER)).thenReturn(Flux.just(TASK_RESPONSE));
        when(taskService.create("board-1",
                new ApiDtos.TaskRequest("Ship feature", "", TaskPriority.MEDIUM, null, null, "column-1"), USER))
                .thenReturn(Mono.just(TASK_RESPONSE));
        when(taskService.update("task-1",
                new ApiDtos.TaskUpdateRequest("Updated", null, null, null, null, null), USER))
                .thenReturn(Mono.just(TASK_RESPONSE));
        when(taskService.move("task-1", new ApiDtos.MoveTaskRequest("column-1"), USER))
                .thenReturn(Mono.just(TASK_RESPONSE));

        authenticatedClient.get().uri("/api/boards/board-1/tasks").exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$[0].title").isEqualTo("Ship feature");

        authenticatedClient.post().uri("/api/boards/board-1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"title":"Ship feature","description":"","priority":"MEDIUM","columnId":"column-1"}
                        """).exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.priority").isEqualTo("MEDIUM");

        authenticatedClient.patch().uri("/api/tasks/task-1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"title":"Updated"}
                        """).exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.id").isEqualTo("task-1");

        authenticatedClient.post().uri("/api/tasks/task-1/move")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"columnId":"column-1"}
                        """).exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.columnId").isEqualTo("column-1");
    }
}
