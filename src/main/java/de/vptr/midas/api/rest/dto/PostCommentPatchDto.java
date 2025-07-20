package de.vptr.midas.api.rest.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO for partially updating post comments.
 * Used by PATCH operations where all fields are optional.
 * 
 * Note: No @NotBlank validation here to allow null values,
 * but we validate non-empty if provided.
 */
public class PostCommentPatchDto {

    @Size(min = 1, max = 10000, message = "Content must be between 1 and 10000 characters when provided")
    public String content;

}
