package de.vptr.midas.api.rest.dto;

import jakarta.validation.constraints.NotBlank;

public class PostCommentUpdateDto {
    @NotBlank
    public String content;
}
