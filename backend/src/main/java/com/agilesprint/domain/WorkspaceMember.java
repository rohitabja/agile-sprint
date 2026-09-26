package com.agilesprint.domain;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document("workspace_members")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceMember {
    @Id
    private String id;
    private String workspaceId;
    private String userId;
    private String username;
    private String email;
    private WorkspaceRole role;
    private Instant joinedAt;
}
