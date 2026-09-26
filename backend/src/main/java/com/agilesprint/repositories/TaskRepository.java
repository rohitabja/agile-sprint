package com.agilesprint.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.agilesprint.domain.Task;

import reactor.core.publisher.Flux;

public interface TaskRepository extends ReactiveMongoRepository<Task, String> {
    Flux<Task> findByBoardIdOrderByColumnIdAscPositionAsc(String boardId);
}
