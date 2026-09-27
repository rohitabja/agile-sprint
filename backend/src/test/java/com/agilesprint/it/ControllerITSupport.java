package com.agilesprint.it;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.agilesprint.controllers.ApiDtos;
import com.agilesprint.domain.TaskPriority;
import com.agilesprint.services.ActivityService;
import com.agilesprint.services.BoardService;
import com.agilesprint.services.CommentService;
import com.agilesprint.services.TaskService;
import com.agilesprint.services.UserContext;
import com.agilesprint.services.WorkspaceService;

@SpringBootTest(
        classes = ControllerITConfiguration.TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("controller-it")
abstract class ControllerITSupport {
    protected static final UserContext USER = new UserContext("alice", "alice", "", "alice");
    protected static final ApiDtos.WorkspaceResponse WORKSPACE_RESPONSE = ApiDtos.WorkspaceResponse.builder()
            .id("workspace-1").name("Sprint").description("").ownerUsername("alice")
            .createdAt(Instant.EPOCH).memberCount(1).build();
    protected static final ApiDtos.MemberResponse MEMBER_RESPONSE = ApiDtos.MemberResponse.builder()
            .id("member-1").userId("bob").username("bob").email("bob@example.com").build();
    protected static final ApiDtos.ColumnResponse COLUMN_RESPONSE = ApiDtos.ColumnResponse.builder()
            .id("column-1").name("To Do").key("todo").position(0).build();
    protected static final ApiDtos.BoardResponse BOARD_RESPONSE = ApiDtos.BoardResponse.builder()
            .id("board-1").workspaceId("workspace-1").name("Main board")
            .columns(List.of(COLUMN_RESPONSE)).build();
    protected static final ApiDtos.TaskResponse TASK_RESPONSE = ApiDtos.TaskResponse.builder()
            .id("task-1").boardId("board-1").workspaceId("workspace-1").columnId("column-1")
            .title("Ship feature").description("").priority(TaskPriority.MEDIUM)
            .createdByUsername("alice").position(0).createdAt(Instant.EPOCH).updatedAt(Instant.EPOCH).build();
    protected static final ApiDtos.CommentResponse COMMENT_RESPONSE = ApiDtos.CommentResponse.builder()
            .id("comment-1").taskId("task-1").authorName("alice").body("Looks good")
            .createdAt(Instant.EPOCH).build();
    protected static final ApiDtos.ActivityResponse ACTIVITY_RESPONSE = ApiDtos.ActivityResponse.builder()
            .id("activity-1").taskId("task-1").actorName("alice").type("TASK_CREATED")
            .message("created task").createdAt(Instant.EPOCH).build();

    @LocalServerPort
    private int port;

    protected WebTestClient client;
    protected WebTestClient authenticatedClient;

    @MockitoBean
    protected ActivityService activityService;
    @MockitoBean
    protected BoardService boardService;
    @MockitoBean
    protected CommentService commentService;
    @MockitoBean
    protected TaskService taskService;
    @MockitoBean
    protected WorkspaceService workspaceService;

    @BeforeEach
    void setUpClient() {
        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
        authenticatedClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port)
                .defaultHeaders(headers -> headers.setBasicAuth("alice", "password"))
                .build();
    }
}
