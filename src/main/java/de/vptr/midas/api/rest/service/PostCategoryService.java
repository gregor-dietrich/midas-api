package de.vptr.midas.api.rest.service;

import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.entity.PostCategoryEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
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
        // Validate name is provided for creation
        if (category.name == null || category.name.trim().isEmpty()) {
            throw new ValidationException("Name is required for creating a category");
        }

        // Validate parent exists if provided
        if (category.parent != null && category.parent.id != null) {
            final PostCategoryEntity existingParent = PostCategoryEntity.findById(category.parent.id);
            if (existingParent == null) {
                throw new WebApplicationException("Parent category not found", Response.Status.BAD_REQUEST);
            }
            category.parent = existingParent;
        }

        category.persist();
        return category;
    }

    @Transactional
    public PostCategoryEntity updateCategory(final PostCategoryEntity category) {
        final PostCategoryEntity existingCategory = PostCategoryEntity.findById(category.id);
        if (existingCategory == null) {
            throw new WebApplicationException("Category not found", Response.Status.NOT_FOUND);
        }

        // Validate name is provided for complete replacement (PUT)
        if (category.name == null || category.name.trim().isEmpty()) {
            throw new ValidationException("Name is required for updating a category");
        }

        // Complete replacement (PUT semantics) - don't change parent in PUT
        existingCategory.name = category.name;

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
        // Note: Don't allow parent changes via PATCH for safety

        existingCategory.persist();
        return existingCategory;
    }

    @Transactional
    public boolean deleteCategory(final Long id) {
        return PostCategoryEntity.deleteById(id);
    }
}
