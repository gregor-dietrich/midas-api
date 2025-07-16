package de.vptr.midas.api.rest.resource;

import java.util.List;

import de.vptr.midas.api.rest.entity.PostCategoryEntity;
import de.vptr.midas.api.rest.service.PostCategoryService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class PostCategoryResource {

    @Inject
    PostCategoryService categoryService;

    @GET
    @Authenticated
    public List<PostCategoryEntity> getAllCategories() {
        return this.categoryService.getAllCategories();
    }

    @GET
    @Path("/root")
    @Authenticated
    public List<PostCategoryEntity> getRootCategories() {
        return this.categoryService.findRootCategories();
    }

    @GET
    @Path("/{id}")
    @Authenticated
    public Response getCategory(@PathParam("id") final Long id) {
        return this.categoryService.findById(id)
                .map(category -> Response.ok(category).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/parent/{parentId}")
    @Authenticated
    public List<PostCategoryEntity> getCategoriesByParent(@PathParam("parentId") final Long parentId) {
        return this.categoryService.findByParentId(parentId);
    }

    @POST
    @RolesAllowed({ "category:add" })
    public Response createCategory(final PostCategoryEntity category) {
        final PostCategoryEntity created = this.categoryService.createCategory(category);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "category:edit" })
    public Response updateCategory(@PathParam("id") final Long id, final PostCategoryEntity category) {
        category.id = id;
        final PostCategoryEntity updated = this.categoryService.updateCategory(category);
        return Response.ok(updated).build();
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "category:edit" })
    public Response patchCategory(@PathParam("id") final Long id, final PostCategoryEntity category) {
        category.id = id;
        final PostCategoryEntity updated = this.categoryService.patchCategory(category);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "category:delete" })
    public Response deleteCategory(@PathParam("id") final Long id) {
        final boolean deleted = this.categoryService.deleteCategory(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
