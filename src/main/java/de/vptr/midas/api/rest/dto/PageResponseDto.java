package de.vptr.midas.api.rest.dto;

import de.vptr.midas.api.rest.entity.PageEntity;

public class PageResponseDto {
    public Long id;
    public String title;
    public String content;

    public PageResponseDto() {
    }

    public PageResponseDto(final PageEntity entity) {
        this.id = entity.id;
        this.title = entity.title;
        this.content = entity.content;
    }
}
