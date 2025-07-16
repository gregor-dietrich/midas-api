package de.vptr.midas.api.rest.service;

import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.entity.PostCategoryEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class PostCategoryService {

    public List<PostCategoryEntity> getAllCategories() {
        return PostCategoryEntity.listAll();
    }

    public Optional<PostCategoryEntity> findById(final Long id) {
        return PostCategoryEntity.findByIdOptional(id);
    }

    public List<PostCategoryEntity> findRootCategories() {
        return PostCategoryEntity.findRootCategories();
    }

    public List<PostCategoryEntity> findByParentId(final Long parentId) {
        return PostCategoryEntity.findByParentId(parentId);
    }

    @Transactional
    public PostCategoryEntity createCategory(final PostCategoryEntity category) {
        category.persist();
        return category;
    }

    @Transactional
    public PostCategoryEntity updateCategory(final PostCategoryEntity category) {
        final PostCategoryEntity existingCategory = PostCategoryEntity.findById(category.id);
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
    public PostCategoryEntity patchCategory(final PostCategoryEntity category) {
        final PostCategoryEntity existingCategory = PostCategoryEntity.findById(category.id);
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
        return PostCategoryEntity.deleteById(id);
    }
}
