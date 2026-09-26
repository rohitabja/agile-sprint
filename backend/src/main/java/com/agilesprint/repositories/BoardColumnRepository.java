package com.agilesprint.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.agilesprint.domain.BoardColumn;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BoardColumnRepository extends ReactiveMongoRepository<BoardColumn, String> {
    Flux<BoardColumn> findByBoardIdOrderByPositionAsc(String boardId);
    Mono<BoardColumn> findByBoardIdAndKey(String boardId, String key);
}
