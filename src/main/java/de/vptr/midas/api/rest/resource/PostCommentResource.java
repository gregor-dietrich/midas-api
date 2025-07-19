package de.vptr.midas.api.rest.resource;

import java.util.List;

import de.vptr.midas.api.rest.entity.PostCommentEntity;
import de.vptr.midas.api.rest.service.PostCommentService;
import de.vptr.midas.api.rest.util.ResponseUtil;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
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
    @RolesAllowed({ "comment:edit", "comment:delete" })
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
    @RolesAllowed({ "post_comment:add" })
    public Response createComment(final PostCommentEntity comment) {
        final PostCommentEntity created = this.commentService.createComment(comment, null);
        return ResponseUtil.created(created);
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "comment:edit" })
    public Response updateComment(@PathParam("id") final Long id, final PostCommentEntity comment) {
        comment.id = id;
        final PostCommentEntity updated = this.commentService.updateComment(comment);
        return ResponseUtil.ok(updated);
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "comment:edit" })
    public Response patchComment(@PathParam("id") final Long id, final PostCommentEntity comment) {
        comment.id = id;
        final PostCommentEntity updated = this.commentService.patchComment(comment);
        return ResponseUtil.ok(updated);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "post_comment:delete" })
    public Response deleteComment(@PathParam("id") final Long id) {
        final boolean deleted = this.commentService.deleteComment(id);
        if (deleted) {
            return ResponseUtil.noContent();
        }
        return ResponseUtil.notFound();
    }
}
