package de.vptr.midas.api.rest.resource;

import java.util.List;

import de.vptr.midas.api.rest.entity.User;
import de.vptr.midas.api.rest.entity.UserGroup;
import de.vptr.midas.api.rest.entity.UserGroupMeta;
import de.vptr.midas.api.rest.service.UserGroupService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/user-groups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class UserGroupResource {

    @Inject
    UserGroupService groupService;

    @GET
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<UserGroup> getAllGroups() {
        return this.groupService.getAllGroups();
    }

    @GET
    @Path("/{id}")
    public Response getGroup(@PathParam("id") final Long id) {
        return this.groupService.findById(id)
                .map(group -> Response.ok(group).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/name/{name}")
    public Response getGroupByName(@PathParam("name") final String name) {
        return this.groupService.findByName(name)
                .map(group -> Response.ok(group).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/{id}/users")
    public List<User> getUsersInGroup(@PathParam("id") final Long groupId) {
        return this.groupService.getUsersInGroup(groupId);
    }

    @GET
    @Path("/user/{userId}")
    public List<UserGroup> getGroupsForUser(@PathParam("userId") final Long userId) {
        return this.groupService.getGroupsForUser(userId);
    }

    @GET
    @Path("/user/{userId}/group/{groupId}/check")
    public Response checkUserInGroup(@PathParam("userId") final Long userId, @PathParam("groupId") final Long groupId) {
        final boolean isInGroup = this.groupService.isUserInGroup(userId, groupId);
        return Response.ok(isInGroup).build();
    }

    @POST
    @RolesAllowed({ "user-group:add" })
    public Response createGroup(final UserGroup group) {
        final UserGroup created = this.groupService.createGroup(group);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "user-group:edit" })
    public Response updateGroup(@PathParam("id") final Long id, final UserGroup group) {
        group.id = id;
        final UserGroup updated = this.groupService.updateGroup(group);
        return Response.ok(updated).build();
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "user-group:edit" })
    public Response patchGroup(@PathParam("id") final Long id, final UserGroup group) {
        group.id = id;
        final UserGroup updated = this.groupService.patchGroup(group);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "user-group:delete" })
    public Response deleteGroup(@PathParam("id") final Long id) {
        final boolean deleted = this.groupService.deleteGroup(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/{groupId}/users/{userId}")
    @RolesAllowed({ "user-group:edit" })
    public Response addUserToGroup(@PathParam("groupId") final Long groupId, @PathParam("userId") final Long userId) {
        final UserGroupMeta meta = this.groupService.addUserToGroup(userId, groupId);
        return Response.status(Response.Status.CREATED).entity(meta).build();
    }

    @DELETE
    @Path("/{groupId}/users/{userId}")
    @RolesAllowed({ "user-group:edit" })
    public Response removeUserFromGroup(@PathParam("groupId") final Long groupId,
            @PathParam("userId") final Long userId) {
        final boolean removed = this.groupService.removeUserFromGroup(userId, groupId);
        if (removed) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
