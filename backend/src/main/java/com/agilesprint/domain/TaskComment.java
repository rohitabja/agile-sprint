package com.agilesprint.domain;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document("task_comments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskComment {
    @Id
    private String id;
    private String taskId;
    private String workspaceId;
    private String authorId;
    private String authorName;
    private String body;
    private Instant createdAt;
}
