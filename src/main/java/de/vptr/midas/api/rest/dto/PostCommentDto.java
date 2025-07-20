package de.vptr.midas.api.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for creating and completely replacing post comments.
 * Used by POST (creation) and PUT (complete replacement) operations.
 * 
 * Note: For POST operations, postId is required.
 * For PUT operations, postId is ignored (comes from URL path).
 */
public class PostCommentDto {

    @NotBlank(message = "Content is required")
    public String content;

    // Required for POST operations (creation)
    // Ignored for PUT operations (postId comes from the URL path)
    public Long postId;

}
