package com.agilesprint.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document("columns")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardColumn {
    @Id
    private String id;
    private String boardId;
    private String name;
    private String key;
    private int position;
}
