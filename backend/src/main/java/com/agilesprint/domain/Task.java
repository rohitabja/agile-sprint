package com.agilesprint.domain;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document("tasks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    private String id;
    private String workspaceId;
    private String boardId;
    private String columnId;
    private String title;
    private String description;
    private TaskPriority priority;
    private String assigneeId;
    private String assigneeUsername;
    private String createdBy;
    private String createdByUsername;
    private int position;
    private Instant createdAt;
    private Instant updatedAt;
}
