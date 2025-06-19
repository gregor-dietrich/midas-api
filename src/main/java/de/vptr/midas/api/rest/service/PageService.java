package de.vptr.midas.api.rest.service;

import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.entity.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class PageService {

    public List<Page> getAllPages() {
        return Page.listAll();
    }

    public Optional<Page> findById(final Long id) {
        return Page.findByIdOptional(id);
    }

    public List<Page> findByTitleContaining(final String title) {
        return Page.findByTitleContaining(title);
    }

    public List<Page> searchContent(final String searchTerm) {
        return Page.searchContent(searchTerm);
    }

    @Transactional
    public Page createPage(final Page page) {
        page.persist();
        return page;
    }

    @Transactional
    public Page updatePage(final Page page) {
        final Page existingPage = Page.findById(page.id);
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
    public Page patchPage(final Page page) {
        final Page existingPage = Page.findById(page.id);
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
        return Page.deleteById(id);
    }
}
