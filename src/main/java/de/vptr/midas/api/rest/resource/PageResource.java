package de.vptr.midas.api.rest.resource;

import java.util.List;

import de.vptr.midas.api.rest.entity.PageEntity;
import de.vptr.midas.api.rest.service.PageService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/pages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class PageResource {

    @Inject
    PageService pageService;

    @GET
    public List<PageEntity> getAllPages() {
        return this.pageService.getAllPages();
    }

    @GET
    @Path("/{id}")
    public Response getPage(@PathParam("id") final Long id) {
        return this.pageService.findById(id)
                .map(page -> Response.ok(page).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/search/title")
    public List<PageEntity> searchByTitle(@QueryParam("q") final String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new BadRequestException("Search query parameter 'q' is required");
        }
        return this.pageService.findByTitleContaining(title);
    }

    @GET
    @Path("/search/content")
    public List<PageEntity> searchContent(@QueryParam("q") final String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new BadRequestException("Search query parameter 'q' is required");
        }
        return this.pageService.searchContent(searchTerm);
    }

    @POST
    @RolesAllowed({ "page:add" })
    public Response createPage(final PageEntity page) {
        final PageEntity created = this.pageService.createPage(page);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "page:edit" })
    public Response updatePage(@PathParam("id") final Long id, final PageEntity page) {
        page.id = id;
        final PageEntity updated = this.pageService.updatePage(page);
        return Response.ok(updated).build();
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "page:edit" })
    public Response patchPage(@PathParam("id") final Long id, final PageEntity page) {
        page.id = id;
        final PageEntity updated = this.pageService.patchPage(page);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "page:delete" })
    public Response deletePage(@PathParam("id") final Long id) {
        final boolean deleted = this.pageService.deletePage(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
