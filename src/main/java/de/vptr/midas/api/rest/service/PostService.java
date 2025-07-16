package de.vptr.midas.api.rest.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.entity.PostEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
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
    public PostEntity createPost(final PostEntity post) {
        post.created = LocalDateTime.now();
        post.lastEdit = post.created;

        if (post.published == null) {
            post.published = false;
        }
        if (post.commentable == null) {
            post.commentable = false;
        }

        post.persist();
        return post;
    }

    @Transactional
    public PostEntity updatePost(final PostEntity post) {
        final PostEntity existingPost = PostEntity.findById(post.id);
        if (existingPost == null) {
            throw new WebApplicationException("Post not found", Response.Status.NOT_FOUND);
        }

        // Complete replacement (PUT semantics)
        existingPost.title = post.title;
        existingPost.content = post.content;
        existingPost.user = post.user;
        existingPost.category = post.category;
        existingPost.published = post.published != null ? post.published : false;
        existingPost.commentable = post.commentable != null ? post.commentable : false;
        existingPost.lastEdit = LocalDateTime.now();

        existingPost.persist();
        return existingPost;
    }

    @Transactional
    public PostEntity patchPost(final PostEntity post) {
        final PostEntity existingPost = PostEntity.findById(post.id);
        if (existingPost == null) {
            throw new WebApplicationException("Post not found", Response.Status.NOT_FOUND);
        }

        // Partial update (PATCH semantics) - only update provided fields
        if (post.title != null) {
            existingPost.title = post.title;
        }
        if (post.content != null) {
            existingPost.content = post.content;
        }
        if (post.user != null) {
            existingPost.user = post.user;
        }
        if (post.category != null) {
            existingPost.category = post.category;
        }
        if (post.published != null) {
            existingPost.published = post.published;
        }
        if (post.commentable != null) {
            existingPost.commentable = post.commentable;
        }

        existingPost.lastEdit = LocalDateTime.now();
        existingPost.persist();
        return existingPost;
    }

    @Transactional
    public boolean deletePost(final Long id) {
        return PostEntity.deleteById(id);
    }
}
