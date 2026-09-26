package com.agilesprint.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.agilesprint.domain.Board;

import reactor.core.publisher.Flux;

public interface BoardRepository extends ReactiveMongoRepository<Board, String> {
    Flux<Board> findByWorkspaceIdOrderByCreatedAtAsc(String workspaceId);
}
