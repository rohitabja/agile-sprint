package com.agilesprint.mappers;

import java.util.List;

import com.agilesprint.controllers.ApiDtos;
import com.agilesprint.domain.Activity;
import com.agilesprint.domain.Board;
import com.agilesprint.domain.BoardColumn;
import com.agilesprint.domain.Task;
import com.agilesprint.domain.TaskComment;
import com.agilesprint.domain.Workspace;
import com.agilesprint.domain.WorkspaceMember;

public final class ApiMapper {
    private ApiMapper() {
    }

    public static ApiDtos.WorkspaceResponse workspace(Workspace workspace, long memberCount) {
        return ApiDtos.WorkspaceResponse.builder()
                .id(workspace.getId())
                .name(workspace.getName())
                .description(workspace.getDescription())
                .ownerUsername(workspace.getOwnerUsername())
                .createdAt(workspace.getCreatedAt())
                .memberCount(memberCount)
                .build();
    }

    public static ApiDtos.MemberResponse member(WorkspaceMember member) {
        return ApiDtos.MemberResponse.builder()
                .id(member.getId())
                .userId(member.getUserId())
                .username(member.getUsername())
                .email(member.getEmail())
                .role(member.getRole())
                .build();
    }

    public static ApiDtos.ColumnResponse column(BoardColumn column) {
        return ApiDtos.ColumnResponse.builder()
                .id(column.getId())
                .name(column.getName())
                .key(column.getKey())
                .position(column.getPosition())
                .build();
    }

    public static ApiDtos.BoardResponse board(Board board, List<BoardColumn> columns) {
        return ApiDtos.BoardResponse.builder()
                .id(board.getId())
                .workspaceId(board.getWorkspaceId())
                .name(board.getName())
                .columns(columns.stream().map(ApiMapper::column).toList())
                .build();
    }

    public static ApiDtos.TaskResponse task(Task task) {
        return ApiDtos.TaskResponse.builder()
                .id(task.getId())
                .boardId(task.getBoardId())
                .workspaceId(task.getWorkspaceId())
                .columnId(task.getColumnId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .assigneeId(task.getAssigneeId())
                .assigneeUsername(task.getAssigneeUsername())
                .createdByUsername(task.getCreatedByUsername())
                .position(task.getPosition())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    public static ApiDtos.CommentResponse comment(TaskComment comment) {
        return ApiDtos.CommentResponse.builder()
                .id(comment.getId())
                .taskId(comment.getTaskId())
                .authorName(comment.getAuthorName())
                .body(comment.getBody())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public static ApiDtos.ActivityResponse activity(Activity activity) {
        return ApiDtos.ActivityResponse.builder()
                .id(activity.getId())
                .taskId(activity.getTaskId())
                .actorName(activity.getActorName())
                .type(activity.getType())
                .message(activity.getMessage())
                .createdAt(activity.getCreatedAt())
                .build();
    }
}
