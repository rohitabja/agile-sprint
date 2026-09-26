package com.agilesprint.domain;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("boards")
public class Board {
    @Id
    private String id;
    private String workspaceId;
    private String name;
    private Instant createdAt;
    public Board() {} public Board(String id,String workspaceId,String name,Instant createdAt){this.id=id;this.workspaceId=workspaceId;this.name=name;this.createdAt=createdAt;}
    public String getId(){return id;} public void setId(String v){id=v;} public String getWorkspaceId(){return workspaceId;} public void setWorkspaceId(String v){workspaceId=v;}
    public String getName(){return name;} public void setName(String v){name=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
    public static Builder builder(){return new Builder();} public static class Builder {private final Board v=new Board(); public Builder id(String x){v.id=x;return this;} public Builder workspaceId(String x){v.workspaceId=x;return this;} public Builder name(String x){v.name=x;return this;} public Builder createdAt(Instant x){v.createdAt=x;return this;} public Board build(){return v;}}
}
