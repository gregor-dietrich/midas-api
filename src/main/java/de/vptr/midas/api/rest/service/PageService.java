package de.vptr.midas.api.rest.service;

import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.entity.PageEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class PageService {

    public List<PageEntity> getAllPages() {
        return PageEntity.listAll();
    }

    public Optional<PageEntity> findById(final Long id) {
        return PageEntity.findByIdOptional(id);
    }

    public List<PageEntity> findByTitleContaining(final String title) {
        return PageEntity.findByTitleContaining(title);
    }

    public List<PageEntity> searchContent(final String searchTerm) {
        return PageEntity.searchContent(searchTerm);
    }

    @Transactional
    public PageEntity createPage(final PageEntity page) {
        page.persist();
        return page;
    }

    @Transactional
    public PageEntity updatePage(final PageEntity page) {
        final PageEntity existingPage = PageEntity.findById(page.id);
        if (existingPage == null) {
            throw new WebApplicationException("Page not found", Response.Status.NOT_FOUND);
        }

        // Complete replacement (PUT semantics)
        existingPage.title = page.title;
        existingPage.content = page.content;

        existingPage.persist();
        return existingPage;
    }

    @Transactional
    public PageEntity patchPage(final PageEntity page) {
        final PageEntity existingPage = PageEntity.findById(page.id);
        if (existingPage == null) {
            throw new WebApplicationException("Page not found", Response.Status.NOT_FOUND);
        }

        // Partial update (PATCH semantics) - only update provided fields
        if (page.title != null) {
            existingPage.title = page.title;
        }
        if (page.content != null) {
            existingPage.content = page.content;
        }

        existingPage.persist();
        return existingPage;
    }

    @Transactional
    public boolean deletePage(final Long id) {
        return PageEntity.deleteById(id);
    }
}
