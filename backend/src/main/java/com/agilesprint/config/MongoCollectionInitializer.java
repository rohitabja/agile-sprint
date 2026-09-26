package com.agilesprint.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import reactor.core.publisher.Flux;

@Configuration
public class MongoCollectionInitializer {
    private static final List<String> COLLECTIONS = List.of(
            "workspaces",
            "workspace_members",
            "boards",
            "columns",
            "tasks",
            "task_comments",
            "activities");

    @Bean
    CommandLineRunner initializeMongoCollections(ReactiveMongoTemplate mongoTemplate) {
        return args -> mongoTemplate.getCollectionNames()
                .collectList()
                .flatMapMany(existing -> Flux.fromIterable(COLLECTIONS)
                        .filter(collection -> !existing.contains(collection))
                        .flatMap(mongoTemplate::createCollection))
                .then()
                .block();
    }
}
