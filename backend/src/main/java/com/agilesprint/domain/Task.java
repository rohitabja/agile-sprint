package com.agilesprint.domain;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("tasks")
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
    public Task() {} public Task(String id,String workspaceId,String boardId,String columnId,String title,String description,TaskPriority priority,String assigneeId,String assigneeUsername,String createdBy,String createdByUsername,int position,Instant createdAt,Instant updatedAt){this.id=id;this.workspaceId=workspaceId;this.boardId=boardId;this.columnId=columnId;this.title=title;this.description=description;this.priority=priority;this.assigneeId=assigneeId;this.assigneeUsername=assigneeUsername;this.createdBy=createdBy;this.createdByUsername=createdByUsername;this.position=position;this.createdAt=createdAt;this.updatedAt=updatedAt;}
    public String getId(){return id;} public void setId(String v){id=v;} public String getWorkspaceId(){return workspaceId;} public void setWorkspaceId(String v){workspaceId=v;} public String getBoardId(){return boardId;} public void setBoardId(String v){boardId=v;} public String getColumnId(){return columnId;} public void setColumnId(String v){columnId=v;} public String getTitle(){return title;} public void setTitle(String v){title=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;} public TaskPriority getPriority(){return priority;} public void setPriority(TaskPriority v){priority=v;} public String getAssigneeId(){return assigneeId;} public void setAssigneeId(String v){assigneeId=v;} public String getAssigneeUsername(){return assigneeUsername;} public void setAssigneeUsername(String v){assigneeUsername=v;} public String getCreatedBy(){return createdBy;} public void setCreatedBy(String v){createdBy=v;} public String getCreatedByUsername(){return createdByUsername;} public void setCreatedByUsername(String v){createdByUsername=v;} public int getPosition(){return position;} public void setPosition(int v){position=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;} public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
    public static Builder builder(){return new Builder();} public static class Builder {private final Task v=new Task(); public Builder id(String x){v.id=x;return this;} public Builder workspaceId(String x){v.workspaceId=x;return this;} public Builder boardId(String x){v.boardId=x;return this;} public Builder columnId(String x){v.columnId=x;return this;} public Builder title(String x){v.title=x;return this;} public Builder description(String x){v.description=x;return this;} public Builder priority(TaskPriority x){v.priority=x;return this;} public Builder assigneeId(String x){v.assigneeId=x;return this;} public Builder assigneeUsername(String x){v.assigneeUsername=x;return this;} public Builder createdBy(String x){v.createdBy=x;return this;} public Builder createdByUsername(String x){v.createdByUsername=x;return this;} public Builder position(int x){v.position=x;return this;} public Builder createdAt(Instant x){v.createdAt=x;return this;} public Builder updatedAt(Instant x){v.updatedAt=x;return this;} public Task build(){return v;}}
}
