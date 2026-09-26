package com.agilesprint.domain;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("workspaces")
public class Workspace {
    @Id
    private String id;
    private String name;
    private String description;
    private String ownerId;
    private String ownerUsername;
    private Instant createdAt;
    private Instant updatedAt;

    public Workspace() {}
    public Workspace(String id, String name, String description, String ownerId, String ownerUsername,
            Instant createdAt, Instant updatedAt) {
        this.id = id; this.name = name; this.description = description; this.ownerId = ownerId;
        this.ownerUsername = ownerUsername; this.createdAt = createdAt; this.updatedAt = updatedAt;
    }
    public String getId() { return id; } public void setId(String value) { id = value; }
    public String getName() { return name; } public void setName(String value) { name = value; }
    public String getDescription() { return description; } public void setDescription(String value) { description = value; }
    public String getOwnerId() { return ownerId; } public void setOwnerId(String value) { ownerId = value; }
    public String getOwnerUsername() { return ownerUsername; } public void setOwnerUsername(String value) { ownerUsername = value; }
    public Instant getCreatedAt() { return createdAt; } public void setCreatedAt(Instant value) { createdAt = value; }
    public Instant getUpdatedAt() { return updatedAt; } public void setUpdatedAt(Instant value) { updatedAt = value; }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Workspace value = new Workspace();
        public Builder id(String v) { value.id = v; return this; } public Builder name(String v) { value.name = v; return this; }
        public Builder description(String v) { value.description = v; return this; } public Builder ownerId(String v) { value.ownerId = v; return this; }
        public Builder ownerUsername(String v) { value.ownerUsername = v; return this; } public Builder createdAt(Instant v) { value.createdAt = v; return this; }
        public Builder updatedAt(Instant v) { value.updatedAt = v; return this; } public Workspace build() { return value; }
    }
}
