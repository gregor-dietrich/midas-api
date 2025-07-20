package de.vptr.midas.api.rest.dto;

import jakarta.validation.constraints.Size;

public class PostDto {

    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    public String title;

    @Size(min = 1, message = "Content must not be empty")
    public String content;

    public Long userId;

    public Long categoryId;

    public Boolean published;

    public Boolean commentable;

    public PostDto() {
    }

    public PostDto(final String title, final String content, final Long userId, final Long categoryId, final Boolean published, final Boolean commentable) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.categoryId = categoryId;
        this.published = published;
        this.commentable = commentable;
    }
}
