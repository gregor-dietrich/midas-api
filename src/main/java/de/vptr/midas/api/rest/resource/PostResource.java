package de.vptr.midas.api.rest.resource;

import java.util.List;
import java.util.stream.Collectors;

import de.vptr.midas.api.rest.dto.PostDto;
import de.vptr.midas.api.rest.dto.PostResponseDto;
import de.vptr.midas.api.rest.service.PostService;
import de.vptr.midas.api.rest.util.ResponseUtil;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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
    public List<PostResponseDto> getAllPosts() {
        return this.postService.getAllPosts()
                .stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/published")
    public List<PostResponseDto> getPublishedPosts() {
        return this.postService.findPublishedPosts()
                .stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Transactional
    public Response getPost(@PathParam("id") final Long id) {
        return ResponseUtil.okOrNotFound(
                this.postService.findById(id).map(PostResponseDto::new));
    }

    @GET
    @Path("/user/{userId}")
    public List<PostResponseDto> getPostsByUser(@PathParam("userId") final Long userId) {
        return this.postService.findByUserId(userId)
                .stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/category/{categoryId}")
    public List<PostResponseDto> getPostsByCategory(@PathParam("categoryId") final Long categoryId) {
        return this.postService.findByCategoryId(categoryId)
                .stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
    }

    @POST
    @RolesAllowed({ "post:add" })
    public Response createPost(@Valid final PostDto postDto) {
        final PostResponseDto created = this.postService.createPost(postDto);
        return ResponseUtil.created(created);
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "post:edit" })
    public Response updatePost(@PathParam("id") final Long id, @Valid final PostDto postDto) {
        final PostResponseDto updated = this.postService.updatePost(id, postDto);
        return ResponseUtil.ok(updated);
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "post:edit" })
    public Response patchPost(@PathParam("id") final Long id, final PostDto postDto) {
        final PostResponseDto updated = this.postService.patchPost(id, postDto);
        return ResponseUtil.ok(updated);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "post:delete" })
    public Response deletePost(@PathParam("id") final Long id) {
        final boolean deleted = this.postService.deletePost(id);
        if (deleted) {
            return ResponseUtil.noContent();
        }
        return ResponseUtil.notFound();
    }
}
