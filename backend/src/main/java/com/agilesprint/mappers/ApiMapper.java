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
        return new ApiDtos.WorkspaceResponse(workspace.getId(), workspace.getName(), workspace.getDescription(),
                workspace.getOwnerUsername(), workspace.getCreatedAt(), memberCount);
    }

    public static ApiDtos.MemberResponse member(WorkspaceMember member) {
        return new ApiDtos.MemberResponse(member.getId(), member.getUserId(), member.getUsername(),
                member.getEmail(), member.getRole());
    }

    public static ApiDtos.ColumnResponse column(BoardColumn column) {
        return new ApiDtos.ColumnResponse(column.getId(), column.getName(), column.getKey(), column.getPosition());
    }

    public static ApiDtos.BoardResponse board(Board board, List<BoardColumn> columns) {
        return new ApiDtos.BoardResponse(board.getId(), board.getWorkspaceId(), board.getName(),
                columns.stream().map(ApiMapper::column).toList());
    }

    public static ApiDtos.TaskResponse task(Task task) {
        return new ApiDtos.TaskResponse(task.getId(), task.getBoardId(), task.getWorkspaceId(), task.getColumnId(),
                task.getTitle(), task.getDescription(), task.getPriority(), task.getAssigneeId(),
                task.getAssigneeUsername(), task.getCreatedByUsername(), task.getPosition(), task.getCreatedAt(),
                task.getUpdatedAt());
    }

    public static ApiDtos.CommentResponse comment(TaskComment comment) {
        return new ApiDtos.CommentResponse(comment.getId(), comment.getTaskId(), comment.getAuthorName(),
                comment.getBody(), comment.getCreatedAt());
    }

    public static ApiDtos.ActivityResponse activity(Activity activity) {
        return new ApiDtos.ActivityResponse(activity.getId(), activity.getTaskId(), activity.getActorName(),
                activity.getType(), activity.getMessage(), activity.getCreatedAt());
    }
}
