package de.vptr.midas.api.rest.dto;

import java.time.LocalDateTime;

import de.vptr.midas.api.rest.entity.PostEntity;

public class PostResponseDto {

    public Long id;
    public String title;
    public String content;
    public Long userId;
    public String username;
    public Long categoryId;
    public String categoryName;
    public Boolean published;
    public Boolean commentable;
    public LocalDateTime created;
    public LocalDateTime lastEdit;
    public Long commentsCount;

    public PostResponseDto() {
    }

    public PostResponseDto(final PostEntity entity) {
        if (entity != null) {
            this.id = entity.id;
            this.title = entity.title;
            this.content = entity.content;
            this.userId = entity.user != null ? entity.user.id : null;
            this.username = entity.user != null ? entity.user.username : null;
            this.categoryId = entity.category != null ? entity.category.id : null;
            this.categoryName = entity.category != null ? entity.category.name : null;
            this.published = entity.published;
            this.commentable = entity.commentable;
            this.created = entity.created;
            this.lastEdit = entity.lastEdit;
            this.commentsCount = entity.comments != null ? (long) entity.comments.size() : 0L;
        }
    }
}
