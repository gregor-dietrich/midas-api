package de.vptr.midas.api.rest.resource;

import java.util.List;

import de.vptr.midas.api.rest.entity.PostEntity;
import de.vptr.midas.api.rest.service.PostService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class PostResource {

    @Inject
    PostService postService;

    @GET
    public List<PostEntity> getAllPosts() {
        return this.postService.getAllPosts();
    }

    @GET
    @Path("/published")
    public List<PostEntity> getPublishedPosts() {
        return this.postService.findPublishedPosts();
    }

    @GET
    @Path("/{id}")
    public Response getPost(@PathParam("id") final Long id) {
        return this.postService.findById(id)
                .map(post -> Response.ok(post).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/user/{userId}")
    public List<PostEntity> getPostsByUser(@PathParam("userId") final Long userId) {
        return this.postService.findByUserId(userId);
    }

    @GET
    @Path("/category/{categoryId}")
    public List<PostEntity> getPostsByCategory(@PathParam("categoryId") final Long categoryId) {
        return this.postService.findByCategoryId(categoryId);
    }

    @POST
    @RolesAllowed({ "post:add" })
    public Response createPost(final PostEntity post) {
        final PostEntity created = this.postService.createPost(post);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "post:edit" })
    public Response updatePost(@PathParam("id") final Long id, final PostEntity post) {
        post.id = id;
        final PostEntity updated = this.postService.updatePost(post);
        return Response.ok(updated).build();
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "post:edit" })
    public Response patchPost(@PathParam("id") final Long id, final PostEntity post) {
        post.id = id;
        final PostEntity updated = this.postService.patchPost(post);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "post:delete" })
    public Response deletePost(@PathParam("id") final Long id) {
        final boolean deleted = this.postService.deletePost(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
