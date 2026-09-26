package com.agilesprint.controllers;

import java.time.Instant;
import java.util.List;

import com.agilesprint.domain.TaskPriority;
import com.agilesprint.domain.WorkspaceRole;

public final class ApiDtos {
    private ApiDtos() {
    }

    public record WorkspaceRequest(String name, String description) {
    }

    public record WorkspaceResponse(String id, String name, String description, String ownerUsername,
            Instant createdAt, long memberCount) {
    }

    public record InviteRequest(String username, String email) {
    }

    public record MemberResponse(String id, String userId, String username, String email, WorkspaceRole role) {
    }

    public record BoardRequest(String name) {
    }

    public record ColumnResponse(String id, String name, String key, int position) {
    }

    public record BoardResponse(String id, String workspaceId, String name, List<ColumnResponse> columns) {
    }

    public record TaskRequest(String title, String description, TaskPriority priority, String assigneeId,
            String assigneeUsername, String columnId) {
    }

    public record TaskUpdateRequest(String title, String description, TaskPriority priority, String assigneeId,
            String assigneeUsername, String columnId) {
    }

    public record MoveTaskRequest(String columnId) {
    }

    public record TaskResponse(String id, String boardId, String workspaceId, String columnId, String title,
            String description, TaskPriority priority, String assigneeId, String assigneeUsername,
            String createdByUsername, int position, Instant createdAt, Instant updatedAt) {
    }

    public record CommentRequest(String body) {
    }

    public record CommentResponse(String id, String taskId, String authorName, String body, Instant createdAt) {
    }

    public record ActivityResponse(String id, String taskId, String actorName, String type, String message,
            Instant createdAt) {
    }
}
