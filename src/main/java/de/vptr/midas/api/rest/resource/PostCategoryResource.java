package de.vptr.midas.api.rest.resource;

import java.util.List;

import de.vptr.midas.api.rest.entity.PostCategoryEntity;
import de.vptr.midas.api.rest.service.PostCategoryService;
import de.vptr.midas.api.rest.util.ResponseUtil;
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
        return ResponseUtil.okOrNotFound(this.categoryService.findById(id));
    }

    @GET
    @Path("/parent/{parentId}")
    @Authenticated
    public List<PostCategoryEntity> getCategoriesByParent(@PathParam("parentId") final Long parentId) {
        return this.categoryService.findByParentId(parentId);
    }

    @POST
    @RolesAllowed({ "post-category:add" })
    public Response createCategory(final PostCategoryEntity category) {
        final PostCategoryEntity created = this.categoryService.createCategory(category);
        return ResponseUtil.created(created);
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "post-category:edit" })
    public Response updateCategory(@PathParam("id") final Long id, final PostCategoryEntity category) {
        category.id = id;
        final PostCategoryEntity updated = this.categoryService.updateCategory(category);
        return ResponseUtil.ok(updated);
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "post-category:edit" })
    public Response patchCategory(@PathParam("id") final Long id, final PostCategoryEntity category) {
        category.id = id;
        final PostCategoryEntity updated = this.categoryService.patchCategory(category);
        return ResponseUtil.ok(updated);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "post-category:delete" })
    public Response deleteCategory(@PathParam("id") final Long id) {
        final boolean deleted = this.categoryService.deleteCategory(id);
        if (deleted) {
            return ResponseUtil.noContent();
        }
        return ResponseUtil.notFound();
    }
}
