package de.vptr.midas.api.rest.dto;

import java.time.LocalDateTime;

import de.vptr.midas.api.rest.entity.PostCommentEntity;

public class PostCommentResponseDto {
    public Long id;
    public String content;
    public Long postId;
    public String postTitle;
    public Long userId;
    public String username;
    public LocalDateTime created;

    public PostCommentResponseDto() {
    }

    public PostCommentResponseDto(PostCommentEntity entity) {
        this.id = entity.id;
        this.content = entity.content;
        this.created = entity.created;
        
        if (entity.post != null) {
            this.postId = entity.post.id;
            this.postTitle = entity.post.title;
        }
        
        if (entity.user != null) {
            this.userId = entity.user.id;
            this.username = entity.user.username;
        }
    }
}
