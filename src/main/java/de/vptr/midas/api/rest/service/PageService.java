package de.vptr.midas.api.rest.service;

import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.dto.PageDto;
import de.vptr.midas.api.rest.dto.PageResponseDto;
import de.vptr.midas.api.rest.entity.PageEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class PageService {

    public List<PageResponseDto> getAllPages() {
        return PageEntity.listAll().stream()
                .map(entity -> new PageResponseDto((PageEntity) entity))
                .toList();
    }

    public Optional<PageResponseDto> findById(final Long id) {
        return PageEntity.findByIdOptional(id)
                .map(entity -> new PageResponseDto((PageEntity) entity));
    }

    public List<PageResponseDto> findByTitleContaining(final String title) {
        return PageEntity.findByTitleContaining(title).stream()
                .map(PageResponseDto::new)
                .toList();
    }

    public List<PageResponseDto> searchContent(final String searchTerm) {
        return PageEntity.searchContent(searchTerm).stream()
                .map(PageResponseDto::new)
                .toList();
    }

    @Transactional
    public PageResponseDto createPage(final PageDto pageDto) {
        if (pageDto.title == null || pageDto.title.trim().isEmpty()) {
            throw new ValidationException("Title is required");
        }
        if (pageDto.content == null || pageDto.content.trim().isEmpty()) {
            throw new ValidationException("Content is required");
        }

        final PageEntity page = new PageEntity();
        page.title = pageDto.title;
        page.content = pageDto.content;
        page.persist();

        return new PageResponseDto(page);
    }

    @Transactional
    public PageResponseDto updatePage(final Long id, final PageDto pageDto) {
        if (pageDto.title == null || pageDto.title.trim().isEmpty()) {
            throw new ValidationException("Title is required");
        }
        if (pageDto.content == null || pageDto.content.trim().isEmpty()) {
            throw new ValidationException("Content is required");
        }

        final PageEntity existingPage = PageEntity.findById(id);
        if (existingPage == null) {
            throw new WebApplicationException("Page not found", Response.Status.NOT_FOUND);
        }

        // Complete replacement (PUT semantics)
        existingPage.title = pageDto.title;
        existingPage.content = pageDto.content;
        existingPage.persist();

        return new PageResponseDto(existingPage);
    }

    @Transactional
    public PageResponseDto patchPage(final Long id, final PageDto pageDto) {
        final PageEntity existingPage = PageEntity.findById(id);
        if (existingPage == null) {
            throw new WebApplicationException("Page not found", Response.Status.NOT_FOUND);
        }

        // Partial update (PATCH semantics) - only update provided fields
        if (pageDto.title != null && !pageDto.title.trim().isEmpty()) {
            existingPage.title = pageDto.title;
        }
        if (pageDto.content != null && !pageDto.content.trim().isEmpty()) {
            existingPage.content = pageDto.content;
        }

        existingPage.persist();
        return new PageResponseDto(existingPage);
    }

    @Transactional
    public boolean deletePage(final Long id) {
        return PageEntity.deleteById(id);
    }
}
