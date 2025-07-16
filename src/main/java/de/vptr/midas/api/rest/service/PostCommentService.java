package de.vptr.midas.api.rest.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.entity.PostCommentEntity;
import de.vptr.midas.api.rest.entity.PostEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class PostCommentService {

    @Inject
    UserService userService;

    public List<PostCommentEntity> getAllComments() {
        return PostCommentEntity.listAll();
    }

    public Optional<PostCommentEntity> findById(final Long id) {
        return PostCommentEntity.findByIdOptional(id);
    }

    public List<PostCommentEntity> findByPostId(final Long postId) {
        return PostCommentEntity.findByPostId(postId);
    }

    public List<PostCommentEntity> findByUserId(final Long userId) {
        return PostCommentEntity.findByUserId(userId);
    }

    public List<PostCommentEntity> findRecentComments(final int limit) {
        return PostCommentEntity.findRecentComments(limit);
    }

    @Transactional
    public PostCommentEntity createComment(final PostCommentEntity comment, final String currentUsername) {
        // Validate post exists
        final PostEntity existingPost = PostEntity.findById(comment.post);
        if (existingPost == null) {
            throw new WebApplicationException("Post with ID " + comment.post + " does not exist.",
                    Response.Status.BAD_REQUEST);
        }

        // Validate post is published
        if (existingPost.published == null || !existingPost.published) {
            throw new WebApplicationException("Cannot add comment to an unpublished post.",
                    Response.Status.BAD_REQUEST);
        }

        // Validate post allows comments
        if (existingPost.commentable == null || !existingPost.commentable) {
            throw new WebApplicationException("Comments are not allowed on this post.", Response.Status.BAD_REQUEST);
        }

        // Auto-assign current user if not provided
        if (comment.user == null) {
            comment.user = this.userService.findByUsername(currentUsername)
                    .orElseThrow(() -> new WebApplicationException("User not found.", Response.Status.BAD_REQUEST));
        }

        comment.created = LocalDateTime.now();
        comment.persist();
        return comment;
    }

    @Transactional
    public PostCommentEntity updateComment(final PostCommentEntity comment) {
        final PostCommentEntity existingComment = PostCommentEntity.findById(comment.id);
        if (existingComment == null) {
            throw new WebApplicationException("Comment not found", Response.Status.NOT_FOUND);
        }

        // Complete replacement (PUT semantics)
        existingComment.content = comment.content;
        existingComment.post = comment.post;
        existingComment.user = comment.user;

        existingComment.persist();
        return existingComment;
    }

    @Transactional
    public PostCommentEntity patchComment(final PostCommentEntity comment) {
        final PostCommentEntity existingComment = PostCommentEntity.findById(comment.id);
        if (existingComment == null) {
            throw new WebApplicationException("Comment not found", Response.Status.NOT_FOUND);
        }

        // Partial update (PATCH semantics) - only update provided fields
        if (comment.content != null) {
            existingComment.content = comment.content;
        }
        if (comment.post != null) {
            existingComment.post = comment.post;
        }
        if (comment.user != null) {
            existingComment.user = comment.user;
        }

        existingComment.persist();
        return existingComment;
    }

    @Transactional
    public boolean deleteComment(final Long id) {
        return PostCommentEntity.deleteById(id);
    }
}
