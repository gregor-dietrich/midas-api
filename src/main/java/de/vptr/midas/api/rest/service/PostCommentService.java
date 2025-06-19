package de.vptr.midas.api.rest.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.entity.Post;
import de.vptr.midas.api.rest.entity.PostComment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class PostCommentService {

    @Inject
    UserService userService;

    public List<PostComment> getAllComments() {
        return PostComment.listAll();
    }

    public Optional<PostComment> findById(final Long id) {
        return PostComment.findByIdOptional(id);
    }

    public List<PostComment> findByPostId(final Long postId) {
        return PostComment.findByPostId(postId);
    }

    public List<PostComment> findByUserId(final Long userId) {
        return PostComment.findByUserId(userId);
    }

    public List<PostComment> findRecentComments(final int limit) {
        return PostComment.findRecentComments(limit);
    }

    @Transactional
    public PostComment createComment(final PostComment comment, final String currentUsername) {
        // Validate post exists
        final Post existingPost = Post.findById(comment.post);
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
    public PostComment updateComment(final PostComment comment) {
        final PostComment existingComment = PostComment.findById(comment.id);
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
    public PostComment patchComment(final PostComment comment) {
        final PostComment existingComment = PostComment.findById(comment.id);
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
        return PostComment.deleteById(id);
    }
}
