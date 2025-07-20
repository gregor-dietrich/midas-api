package de.vptr.midas.api.rest.resource;

import java.util.List;

import de.vptr.midas.api.rest.dto.PostCommentDto;
import de.vptr.midas.api.rest.dto.PostCommentPatchDto;
import de.vptr.midas.api.rest.dto.PostCommentResponseDto;
import de.vptr.midas.api.rest.entity.PostCommentEntity;
import de.vptr.midas.api.rest.entity.PostEntity;
import de.vptr.midas.api.rest.service.PostCommentService;
import de.vptr.midas.api.rest.util.ResponseUtil;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/comments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class PostCommentResource {

    @Inject
    SecurityContext securityContext;

    @Inject
    PostCommentService commentService;

    @GET
    @RolesAllowed({ "post-comment:edit", "post-comment:delete" })
    public List<PostCommentEntity> getAllComments() {
        return this.commentService.getAllComments();
    }

    @GET
    @Path("/{id}")
    public Response getComment(@PathParam("id") final Long id) {
        return ResponseUtil.okOrNotFound(this.commentService.findById(id));
    }

    @GET
    @Path("/post/{postId}")
    @Authenticated
    public List<PostCommentEntity> getCommentsByPost(@PathParam("postId") final Long postId) {
        return this.commentService.findByPostId(postId);
    }

    @GET
    @Path("/user/{userId}")
    @Authenticated
    public List<PostCommentEntity> getCommentsByUser(@PathParam("userId") final Long userId) {
        return this.commentService.findByUserId(userId);
    }

    @GET
    @Path("/recent")
    @Authenticated
    public List<PostCommentEntity> getRecentComments(@QueryParam("limit") @DefaultValue("10") final int limit) {
        return this.commentService.findRecentComments(limit);
    }

    @POST
    @RolesAllowed({ "post-comment:add" })
    public Response createComment(@Valid final PostCommentDto commentDto) {
        // Map DTO to entity
        final PostCommentEntity comment = new PostCommentEntity();
        comment.content = commentDto.content;
        if (commentDto.postId != null) {
            final PostEntity post = new PostEntity();
            post.id = commentDto.postId;
            comment.post = post;
        }
        // Get current username from security context
        final String currentUsername = this.securityContext.getUserPrincipal() != null
                ? this.securityContext.getUserPrincipal().getName()
                : null;
        final PostCommentEntity created = this.commentService.createComment(comment, currentUsername);
        final PostCommentResponseDto responseDto = new PostCommentResponseDto(created);
        return ResponseUtil.created(responseDto);
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "post-comment:edit" })
    public Response updateComment(@PathParam("id") final Long id, @Valid final PostCommentDto commentDto) {
        // Map DTO to entity
        final PostCommentEntity comment = new PostCommentEntity();
        comment.id = id;
        comment.content = commentDto.content;

        final PostCommentEntity updated = this.commentService.updateComment(comment);
        final PostCommentResponseDto responseDto = new PostCommentResponseDto(updated);
        return ResponseUtil.ok(responseDto);
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "post-comment:edit" })
    public Response patchComment(@PathParam("id") final Long id, @Valid final PostCommentPatchDto commentDto) {
        // Map DTO to entity
        final PostCommentEntity comment = new PostCommentEntity();
        comment.id = id;
        comment.content = commentDto.content;

        final PostCommentEntity updated = this.commentService.patchComment(comment);
        final PostCommentResponseDto responseDto = new PostCommentResponseDto(updated);
        return ResponseUtil.ok(responseDto);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "post-comment:delete" })
    public Response deleteComment(@PathParam("id") final Long id) {
        final boolean deleted = this.commentService.deleteComment(id);
        if (deleted) {
            return ResponseUtil.noContent();
        }
        return ResponseUtil.notFound();
    }
}
