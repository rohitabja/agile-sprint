package com.agilesprint.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.agilesprint.controllers.ApiDtos;
import com.agilesprint.domain.Board;
import com.agilesprint.domain.BoardColumn;
import com.agilesprint.domain.Task;
import com.agilesprint.domain.TaskPriority;
import com.agilesprint.domain.Workspace;
import com.agilesprint.domain.WorkspaceRole;

class ApiMapperTest {
    @Test
    void mapsWorkspaceFieldsAndMemberCount() {
        Instant createdAt = Instant.parse("2026-01-02T03:04:05Z");
        Workspace workspace = Workspace.builder().id("workspace-1").name("Sprint").description("Team board")
                .ownerUsername("alice").createdAt(createdAt).build();

        ApiDtos.WorkspaceResponse result = ApiMapper.workspace(workspace, 3);

        assertEquals("workspace-1", result.id());
        assertEquals("Sprint", result.name());
        assertEquals("alice", result.ownerUsername());
        assertEquals(createdAt, result.createdAt());
        assertEquals(3, result.memberCount());
    }

    @Test
    void mapsBoardColumnsAndTaskPriority() {
        Board board = Board.builder().id("board-1").workspaceId("workspace-1").name("Main").build();
        BoardColumn column = BoardColumn.builder().id("column-1").name("To Do").key("todo").position(0).build();
        Task task = Task.builder().id("task-1").boardId("board-1").workspaceId("workspace-1")
                .columnId("column-1").title("Prepare release").priority(TaskPriority.HIGH).build();

        ApiDtos.BoardResponse mappedBoard = ApiMapper.board(board, List.of(column));
        ApiDtos.TaskResponse mappedTask = ApiMapper.task(task);

        assertEquals("todo", mappedBoard.columns().getFirst().key());
        assertEquals("task-1", mappedTask.id());
        assertEquals(TaskPriority.HIGH, mappedTask.priority());
        assertEquals("column-1", mappedTask.columnId());
    }

    @Test
    void mapsWorkspaceMemberRole() {
        var member = com.agilesprint.domain.WorkspaceMember.builder().id("member-1").username("alice")
                .role(WorkspaceRole.ADMIN).build();

        assertEquals(WorkspaceRole.ADMIN, ApiMapper.member(member).role());
    }
}
