package de.vptr.midas.api.rest.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.dto.PostDto;
import de.vptr.midas.api.rest.dto.PostResponseDto;
import de.vptr.midas.api.rest.entity.PostCategoryEntity;
import de.vptr.midas.api.rest.entity.PostEntity;
import de.vptr.midas.api.rest.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class PostService {

    public List<PostEntity> getAllPosts() {
        return PostEntity.listAll();
    }

    public Optional<PostEntity> findById(final Long id) {
        return PostEntity.findByIdOptional(id);
    }

    public List<PostEntity> findPublishedPosts() {
        return PostEntity.find("published = true").list();
    }

    public List<PostEntity> findByUserId(final Long userId) {
        return PostEntity.find("user.id", userId).list();
    }

    public List<PostEntity> findByCategoryId(final Long categoryId) {
        return PostEntity.find("category.id", categoryId).list();
    }

    @Transactional
    public PostResponseDto createPost(final PostDto postDto) {
        // Validate required fields for POST
        if (postDto.title == null || postDto.title.trim().isEmpty()) {
            throw new ValidationException("Title is required for creating a post");
        }
        if (postDto.content == null || postDto.content.trim().isEmpty()) {
            throw new ValidationException("Content is required for creating a post");
        }

        final PostEntity post = new PostEntity();
        post.title = postDto.title;
        post.content = postDto.content;
        post.published = postDto.published != null ? postDto.published : false;
        post.commentable = postDto.commentable != null ? postDto.commentable : false;
        post.created = LocalDateTime.now();
        post.lastEdit = post.created;

        // Set user if provided
        if (postDto.userId != null) {
            final UserEntity user = UserEntity.findById(postDto.userId);
            if (user == null) {
                throw new ValidationException("User with ID " + postDto.userId + " not found");
            }
            post.user = user;
        }

        // Set category if provided
        if (postDto.categoryId != null) {
            final PostCategoryEntity category = PostCategoryEntity.findById(postDto.categoryId);
            if (category == null) {
                throw new ValidationException("Category with ID " + postDto.categoryId + " not found");
            }
            post.category = category;
        }

        post.persist();
        return new PostResponseDto(post);
    }

    @Transactional
    public PostResponseDto updatePost(final Long id, final PostDto postDto) {
        // Validate required fields for PUT
        if (postDto.title == null || postDto.title.trim().isEmpty()) {
            throw new ValidationException("Title is required for updating a post");
        }
        if (postDto.content == null || postDto.content.trim().isEmpty()) {
            throw new ValidationException("Content is required for updating a post");
        }

        final PostEntity existingPost = PostEntity.findById(id);
        if (existingPost == null) {
            throw new WebApplicationException("Post not found", Response.Status.NOT_FOUND);
        }

        // Complete replacement (PUT semantics)
        existingPost.title = postDto.title;
        existingPost.content = postDto.content;
        existingPost.published = postDto.published != null ? postDto.published : false;
        existingPost.commentable = postDto.commentable != null ? postDto.commentable : false;
        existingPost.lastEdit = LocalDateTime.now();

        // Set user if provided
        if (postDto.userId != null) {
            final UserEntity user = UserEntity.findById(postDto.userId);
            if (user == null) {
                throw new ValidationException("User with ID " + postDto.userId + " not found");
            }
            existingPost.user = user;
        } else {
            existingPost.user = null;
        }

        // Set category if provided
        if (postDto.categoryId != null) {
            final PostCategoryEntity category = PostCategoryEntity.findById(postDto.categoryId);
            if (category == null) {
                throw new ValidationException("Category with ID " + postDto.categoryId + " not found");
            }
            existingPost.category = category;
        } else {
            existingPost.category = null;
        }

        existingPost.persist();
        return new PostResponseDto(existingPost);
    }

    @Transactional
    public PostResponseDto patchPost(final Long id, final PostDto postDto) {
        final PostEntity existingPost = PostEntity.findById(id);
        if (existingPost == null) {
            throw new WebApplicationException("Post not found", Response.Status.NOT_FOUND);
        }

        // Partial update (PATCH semantics) - only update provided fields
        if (postDto.title != null && !postDto.title.trim().isEmpty()) {
            existingPost.title = postDto.title;
        }
        if (postDto.content != null && !postDto.content.trim().isEmpty()) {
            existingPost.content = postDto.content;
        }
        if (postDto.published != null) {
            existingPost.published = postDto.published;
        }
        if (postDto.commentable != null) {
            existingPost.commentable = postDto.commentable;
        }

        // Set user if provided
        if (postDto.userId != null) {
            final UserEntity user = UserEntity.findById(postDto.userId);
            if (user == null) {
                throw new ValidationException("User with ID " + postDto.userId + " not found");
            }
            existingPost.user = user;
        }

        // Set category if provided
        if (postDto.categoryId != null) {
            final PostCategoryEntity category = PostCategoryEntity.findById(postDto.categoryId);
            if (category == null) {
                throw new ValidationException("Category with ID " + postDto.categoryId + " not found");
            }
            existingPost.category = category;
        }

        existingPost.lastEdit = LocalDateTime.now();
        existingPost.persist();
        return new PostResponseDto(existingPost);
    }

    @Transactional
    public boolean deletePost(final Long id) {
        return PostEntity.deleteById(id);
    }
}
