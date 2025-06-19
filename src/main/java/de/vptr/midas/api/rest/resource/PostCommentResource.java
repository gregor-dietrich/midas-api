package de.vptr.midas.api.rest.resource;

import java.util.List;

import de.vptr.midas.api.rest.entity.PostComment;
import de.vptr.midas.api.rest.service.PostCommentService;
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
    public List<PostComment> getAllComments() {
        return this.commentService.getAllComments();
    }

    @GET
    @Path("/{id}")
    @Authenticated
    public Response getComment(@PathParam("id") final Long id) {
        return this.commentService.findById(id)
                .map(comment -> Response.ok(comment).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/post/{postId}")
    @Authenticated
    public List<PostComment> getCommentsByPost(@PathParam("postId") final Long postId) {
        return this.commentService.findByPostId(postId);
    }

    @GET
    @Path("/user/{userId}")
    @Authenticated
    public List<PostComment> getCommentsByUser(@PathParam("userId") final Long userId) {
        return this.commentService.findByUserId(userId);
    }

    @GET
    @Path("/recent")
    @Authenticated
    public List<PostComment> getRecentComments(@QueryParam("limit") @DefaultValue("10") final int limit) {
        return this.commentService.findRecentComments(limit);
    }

    @POST
    @RolesAllowed({ "comment:add" })
    public Response createComment(final PostComment comment) {
        try {
            final var username = this.securityContext.getUserPrincipal().getName();
            final PostComment created = this.commentService.createComment(comment, username);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (final WebApplicationException e) {
            return Response.status(e.getResponse().getStatus()).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "comment:edit" })
    public Response updateComment(@PathParam("id") final Long id, final PostComment comment) {
        comment.id = id;
        final PostComment updated = this.commentService.updateComment(comment);
        return Response.ok(updated).build();
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "comment:edit" })
    public Response patchComment(@PathParam("id") final Long id, final PostComment comment) {
        comment.id = id;
        final PostComment updated = this.commentService.patchComment(comment);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "comment:delete" })
    public Response deleteComment(@PathParam("id") final Long id) {
        final boolean deleted = this.commentService.deleteComment(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
