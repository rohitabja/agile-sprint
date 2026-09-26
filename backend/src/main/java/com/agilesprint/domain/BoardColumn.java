package com.agilesprint.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("columns")
public class BoardColumn {
    @Id
    private String id;
    private String boardId;
    private String name;
    private String key;
    private int position;
    public BoardColumn() {} public BoardColumn(String id,String boardId,String name,String key,int position){this.id=id;this.boardId=boardId;this.name=name;this.key=key;this.position=position;}
    public String getId(){return id;} public void setId(String v){id=v;} public String getBoardId(){return boardId;} public void setBoardId(String v){boardId=v;} public String getName(){return name;} public void setName(String v){name=v;} public String getKey(){return key;} public void setKey(String v){key=v;} public int getPosition(){return position;} public void setPosition(int v){position=v;}
    public static Builder builder(){return new Builder();} public static class Builder {private final BoardColumn v=new BoardColumn(); public Builder id(String x){v.id=x;return this;} public Builder boardId(String x){v.boardId=x;return this;} public Builder name(String x){v.name=x;return this;} public Builder key(String x){v.key=x;return this;} public Builder position(int x){v.position=x;return this;} public BoardColumn build(){return v;}}
}
