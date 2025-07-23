package de.vptr.midas.api.rest.resource;

import java.util.List;
import java.util.stream.Collectors;

import de.vptr.midas.api.rest.dto.UserRankDto;
import de.vptr.midas.api.rest.dto.UserRankResponseDto;
import de.vptr.midas.api.rest.service.UserRankService;
import de.vptr.midas.api.rest.util.ResponseUtil;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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
    public List<UserRankResponseDto> getAllRanks() {
        return this.rankService.getAllRanks()
                .stream()
                .map(UserRankResponseDto::new)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    public Response getRank(@PathParam("id") final Long id) {
        return ResponseUtil.okOrNotFound(
                this.rankService.findById(id).map(UserRankResponseDto::new));
    }

    @GET
    @Path("/name/{name}")
    public Response getRankByName(@PathParam("name") final String name) {
        return ResponseUtil.okOrNotFound(
                this.rankService.findByName(name).map(UserRankResponseDto::new));
    }

    @POST
    @RolesAllowed({ "user-rank:add" })
    public Response createRank(@Valid final UserRankDto rankDto) {
        final UserRankResponseDto created = this.rankService.createRank(rankDto);
        return ResponseUtil.created(created);
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "user-rank:edit" })
    public Response updateRank(@PathParam("id") final Long id, @Valid final UserRankDto rankDto) {
        final UserRankResponseDto updated = this.rankService.updateRank(id, rankDto);
        return ResponseUtil.ok(updated);
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "user-rank:edit" })
    public Response patchRank(@PathParam("id") final Long id, final UserRankDto rankDto) {
        final UserRankResponseDto updated = this.rankService.patchRank(id, rankDto);
        return ResponseUtil.ok(updated);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "user-rank:delete" })
    public Response deleteRank(@PathParam("id") final Long id) {
        final boolean deleted = this.rankService.deleteRank(id);
        if (deleted) {
            return ResponseUtil.noContent();
        }
        return ResponseUtil.notFound();
    }
}
