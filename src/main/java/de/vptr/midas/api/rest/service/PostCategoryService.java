package de.vptr.midas.api.rest.service;

import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.entity.PostCategory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class PostCategoryService {

    public List<PostCategory> getAllCategories() {
        return PostCategory.listAll();
    }

    public Optional<PostCategory> findById(final Long id) {
        return PostCategory.findByIdOptional(id);
    }

    public List<PostCategory> findRootCategories() {
        return PostCategory.findRootCategories();
    }

    public List<PostCategory> findByParentId(final Long parentId) {
        return PostCategory.findByParentId(parentId);
    }

    @Transactional
    public PostCategory createCategory(final PostCategory category) {
        category.persist();
        return category;
    }

    @Transactional
    public PostCategory updateCategory(final PostCategory category) {
        final PostCategory existingCategory = PostCategory.findById(category.id);
        if (existingCategory == null) {
            throw new WebApplicationException("Category not found", Response.Status.NOT_FOUND);
        }

        // Complete replacement (PUT semantics)
        existingCategory.name = category.name;
        existingCategory.parent = category.parent;

        existingCategory.persist();
        return existingCategory;
    }

    @Transactional
    public PostCategory patchCategory(final PostCategory category) {
        final PostCategory existingCategory = PostCategory.findById(category.id);
        if (existingCategory == null) {
            throw new WebApplicationException("Category not found", Response.Status.NOT_FOUND);
        }

        // Partial update (PATCH semantics) - only update provided fields
        if (category.name != null) {
            existingCategory.name = category.name;
        }
        if (category.parent != null) {
            existingCategory.parent = category.parent;
        }

        existingCategory.persist();
        return existingCategory;
    }

    @Transactional
    public boolean deleteCategory(final Long id) {
        return PostCategory.deleteById(id);
    }
}
