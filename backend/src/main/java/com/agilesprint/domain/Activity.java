package com.agilesprint.domain;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("activities")
public class Activity {
    @Id
    private String id;
    private String workspaceId;
    private String taskId;
    private String actorId;
    private String actorName;
    private String type;
    private String message;
    private Instant createdAt;
    public Activity() {} public Activity(String id,String workspaceId,String taskId,String actorId,String actorName,String type,String message,Instant createdAt){this.id=id;this.workspaceId=workspaceId;this.taskId=taskId;this.actorId=actorId;this.actorName=actorName;this.type=type;this.message=message;this.createdAt=createdAt;}
    public String getId(){return id;} public void setId(String v){id=v;} public String getWorkspaceId(){return workspaceId;} public void setWorkspaceId(String v){workspaceId=v;} public String getTaskId(){return taskId;} public void setTaskId(String v){taskId=v;} public String getActorId(){return actorId;} public void setActorId(String v){actorId=v;} public String getActorName(){return actorName;} public void setActorName(String v){actorName=v;} public String getType(){return type;} public void setType(String v){type=v;} public String getMessage(){return message;} public void setMessage(String v){message=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
    public static Builder builder(){return new Builder();} public static class Builder {private final Activity v=new Activity(); public Builder id(String x){v.id=x;return this;} public Builder workspaceId(String x){v.workspaceId=x;return this;} public Builder taskId(String x){v.taskId=x;return this;} public Builder actorId(String x){v.actorId=x;return this;} public Builder actorName(String x){v.actorName=x;return this;} public Builder type(String x){v.type=x;return this;} public Builder message(String x){v.message=x;return this;} public Builder createdAt(Instant x){v.createdAt=x;return this;} public Activity build(){return v;}}
}
