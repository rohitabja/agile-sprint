package com.agilesprint.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.agilesprint.domain.Activity;

import reactor.core.publisher.Flux;

public interface ActivityRepository extends ReactiveMongoRepository<Activity, String> {
    Flux<Activity> findByWorkspaceIdOrderByCreatedAtDesc(String workspaceId);
}
