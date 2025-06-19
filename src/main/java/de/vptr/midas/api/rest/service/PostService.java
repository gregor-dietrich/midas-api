package de.vptr.midas.api.rest.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.entity.Post;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class PostService {

    public List<Post> getAllPosts() {
        return Post.listAll();
    }

    public Optional<Post> findById(final Long id) {
        return Post.findByIdOptional(id);
    }

    public List<Post> findPublishedPosts() {
        return Post.find("published = true").list();
    }

    public List<Post> findByUserId(final Long userId) {
        return Post.find("user.id", userId).list();
    }

    public List<Post> findByCategoryId(final Long categoryId) {
        return Post.find("category.id", categoryId).list();
    }

    @Transactional
    public Post createPost(final Post post) {
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
    public Post updatePost(final Post post) {
        final Post existingPost = Post.findById(post.id);
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
    public Post patchPost(final Post post) {
        final Post existingPost = Post.findById(post.id);
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
        return Post.deleteById(id);
    }
}
