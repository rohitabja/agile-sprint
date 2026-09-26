package com.agilesprint.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.agilesprint.domain.TaskComment;

import reactor.core.publisher.Flux;

public interface TaskCommentRepository extends ReactiveMongoRepository<TaskComment, String> {
    Flux<TaskComment> findByTaskIdOrderByCreatedAtAsc(String taskId);
}
