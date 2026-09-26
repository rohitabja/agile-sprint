package com.agilesprint.domain;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("workspace_members")
public class WorkspaceMember {
    @Id
    private String id;
    private String workspaceId;
    private String userId;
    private String username;
    private String email;
    private WorkspaceRole role;
    private Instant joinedAt;

    public WorkspaceMember() {}
    public WorkspaceMember(String id, String workspaceId, String userId, String username, String email,
            WorkspaceRole role, Instant joinedAt) {
        this.id=id; this.workspaceId=workspaceId; this.userId=userId; this.username=username; this.email=email;
        this.role=role; this.joinedAt=joinedAt;
    }
    public String getId(){return id;} public void setId(String v){id=v;}
    public String getWorkspaceId(){return workspaceId;} public void setWorkspaceId(String v){workspaceId=v;}
    public String getUserId(){return userId;} public void setUserId(String v){userId=v;}
    public String getUsername(){return username;} public void setUsername(String v){username=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public WorkspaceRole getRole(){return role;} public void setRole(WorkspaceRole v){role=v;}
    public Instant getJoinedAt(){return joinedAt;} public void setJoinedAt(Instant v){joinedAt=v;}
    public static Builder builder(){return new Builder();} public static class Builder { private final WorkspaceMember v=new WorkspaceMember();
        public Builder id(String x){v.id=x;return this;} public Builder workspaceId(String x){v.workspaceId=x;return this;} public Builder userId(String x){v.userId=x;return this;}
        public Builder username(String x){v.username=x;return this;} public Builder email(String x){v.email=x;return this;} public Builder role(WorkspaceRole x){v.role=x;return this;}
        public Builder joinedAt(Instant x){v.joinedAt=x;return this;} public WorkspaceMember build(){return v;}}
}
