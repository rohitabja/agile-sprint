package com.agilesprint.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.agilesprint.domain.WorkspaceMember;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WorkspaceMemberRepository extends ReactiveMongoRepository<WorkspaceMember, String> {
    Flux<WorkspaceMember> findByUserId(String userId);
    Flux<WorkspaceMember> findByWorkspaceId(String workspaceId);
    Flux<WorkspaceMember> findByUsername(String username);
    Mono<WorkspaceMember> findByWorkspaceIdAndUserId(String workspaceId, String userId);
    Mono<WorkspaceMember> findByWorkspaceIdAndUsername(String workspaceId, String username);
    Mono<Boolean> existsByWorkspaceIdAndUsername(String workspaceId, String username);
}
