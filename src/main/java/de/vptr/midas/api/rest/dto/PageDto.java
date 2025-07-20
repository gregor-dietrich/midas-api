package de.vptr.midas.api.rest.dto;

import jakarta.validation.constraints.NotBlank;

public class PageDto {
    @NotBlank(message = "Title is required")
    public String title;

    @NotBlank(message = "Content is required")
    public String content;
}
