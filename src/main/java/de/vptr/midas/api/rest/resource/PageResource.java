package de.vptr.midas.api.rest.resource;

import java.util.List;

import de.vptr.midas.api.rest.dto.PageDto;
import de.vptr.midas.api.rest.dto.PageResponseDto;
import de.vptr.midas.api.rest.service.PageService;
import de.vptr.midas.api.rest.util.ResponseUtil;
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
    public List<PageResponseDto> getAllPages() {
        return this.pageService.getAllPages();
    }

    @GET
    @Path("/{id}")
    public Response getPage(@PathParam("id") final Long id) {
        return ResponseUtil.okOrNotFound(this.pageService.findById(id));
    }

    @GET
    @Path("/search/title")
    public List<PageResponseDto> searchByTitle(@QueryParam("q") final String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new BadRequestException("Search query parameter 'q' is required");
        }
        return this.pageService.findByTitleContaining(title);
    }

    @GET
    @Path("/search/content")
    public List<PageResponseDto> searchContent(@QueryParam("q") final String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new BadRequestException("Search query parameter 'q' is required");
        }
        return this.pageService.searchContent(searchTerm);
    }

    @POST
    @RolesAllowed({ "page:add" })
    public Response createPage(final PageDto pageDto) {
        final PageResponseDto created = this.pageService.createPage(pageDto);
        return ResponseUtil.created(created);
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "page:edit" })
    public Response updatePage(@PathParam("id") final Long id, final PageDto pageDto) {
        final PageResponseDto updated = this.pageService.updatePage(id, pageDto);
        return ResponseUtil.ok(updated);
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "page:edit" })
    public Response patchPage(@PathParam("id") final Long id, final PageDto pageDto) {
        final PageResponseDto updated = this.pageService.patchPage(id, pageDto);
        return ResponseUtil.ok(updated);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "page:delete" })
    public Response deletePage(@PathParam("id") final Long id) {
        final boolean deleted = this.pageService.deletePage(id);
        if (deleted) {
            return ResponseUtil.noContent();
        }
        return ResponseUtil.notFound();
    }
}
