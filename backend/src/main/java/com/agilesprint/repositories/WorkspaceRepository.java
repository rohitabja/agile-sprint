package com.agilesprint.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.agilesprint.domain.Workspace;

public interface WorkspaceRepository extends ReactiveMongoRepository<Workspace, String> {
}
