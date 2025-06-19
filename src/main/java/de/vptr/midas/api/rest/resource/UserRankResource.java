package de.vptr.midas.api.rest.resource;

import java.util.List;

import de.vptr.midas.api.rest.entity.UserRank;
import de.vptr.midas.api.rest.service.UserRankService;
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
    public List<UserRank> getAllRanks() {
        return this.rankService.getAllRanks();
    }

    @GET
    @Path("/{id}")
    public Response getRank(@PathParam("id") final Long id) {
        return this.rankService.findById(id)
                .map(rank -> Response.ok(rank).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/name/{name}")
    public Response getRankByName(@PathParam("name") final String name) {
        return this.rankService.findByName(name)
                .map(rank -> Response.ok(rank).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @RolesAllowed({ "user_rank:add" })
    public Response createRank(final UserRank rank) {
        final UserRank created = this.rankService.createRank(rank);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "user_rank:edit" })
    public Response updateRank(@PathParam("id") final Long id, final UserRank rank) {
        rank.id = id;
        final UserRank updated = this.rankService.updateRank(rank);
        return Response.ok(updated).build();
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "user_rank:edit" })
    public Response patchRank(@PathParam("id") final Long id, final UserRank rank) {
        rank.id = id;
        final UserRank updated = this.rankService.patchRank(rank);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "user_rank:delete" })
    public Response deleteRank(@PathParam("id") final Long id) {
        final boolean deleted = this.rankService.deleteRank(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
