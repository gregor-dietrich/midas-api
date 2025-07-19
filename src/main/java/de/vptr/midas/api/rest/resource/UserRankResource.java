package de.vptr.midas.api.rest.resource;

import java.util.List;

import de.vptr.midas.api.rest.entity.UserRankEntity;
import de.vptr.midas.api.rest.service.UserRankService;
import de.vptr.midas.api.rest.util.ResponseUtil;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/user-ranks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class UserRankResource {

    @Inject
    UserRankService rankService;

    @GET
    public List<UserRankEntity> getAllRanks() {
        return this.rankService.getAllRanks();
    }

    @GET
    @Path("/{id}")
    public Response getRank(@PathParam("id") final Long id) {
        return ResponseUtil.okOrNotFound(this.rankService.findById(id));
    }

    @GET
    @Path("/name/{name}")
    public Response getRankByName(@PathParam("name") final String name) {
        return ResponseUtil.okOrNotFound(this.rankService.findByName(name));
    }

    @POST
    @RolesAllowed({ "user_rank:add" })
    public Response createRank(final UserRankEntity rank) {
        final UserRankEntity created = this.rankService.createRank(rank);
        return ResponseUtil.created(created);
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "user_rank:edit" })
    public Response updateRank(@PathParam("id") final Long id, final UserRankEntity rank) {
        rank.id = id;
        final UserRankEntity updated = this.rankService.updateRank(rank);
        return ResponseUtil.ok(updated);
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "user_rank:edit" })
    public Response patchRank(@PathParam("id") final Long id, final UserRankEntity rank) {
        rank.id = id;
        final UserRankEntity updated = this.rankService.patchRank(rank);
        return ResponseUtil.ok(updated);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "user_rank:delete" })
    public Response deleteRank(@PathParam("id") final Long id) {
        final boolean deleted = this.rankService.deleteRank(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return ResponseUtil.notFound();
    }
}
