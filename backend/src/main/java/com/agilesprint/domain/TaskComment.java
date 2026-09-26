package com.agilesprint.domain;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("task_comments")
public class TaskComment {
    @Id
    private String id;
    private String taskId;
    private String workspaceId;
    private String authorId;
    private String authorName;
    private String body;
    private Instant createdAt;
    public TaskComment() {} public TaskComment(String id,String taskId,String workspaceId,String authorId,String authorName,String body,Instant createdAt){this.id=id;this.taskId=taskId;this.workspaceId=workspaceId;this.authorId=authorId;this.authorName=authorName;this.body=body;this.createdAt=createdAt;}
    public String getId(){return id;} public void setId(String v){id=v;} public String getTaskId(){return taskId;} public void setTaskId(String v){taskId=v;} public String getWorkspaceId(){return workspaceId;} public void setWorkspaceId(String v){workspaceId=v;} public String getAuthorId(){return authorId;} public void setAuthorId(String v){authorId=v;} public String getAuthorName(){return authorName;} public void setAuthorName(String v){authorName=v;} public String getBody(){return body;} public void setBody(String v){body=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
    public static Builder builder(){return new Builder();} public static class Builder {private final TaskComment v=new TaskComment(); public Builder id(String x){v.id=x;return this;} public Builder taskId(String x){v.taskId=x;return this;} public Builder workspaceId(String x){v.workspaceId=x;return this;} public Builder authorId(String x){v.authorId=x;return this;} public Builder authorName(String x){v.authorName=x;return this;} public Builder body(String x){v.body=x;return this;} public Builder createdAt(Instant x){v.createdAt=x;return this;} public TaskComment build(){return v;}}
}
