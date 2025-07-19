package de.vptr.midas.api.rest.resource;

import java.util.List;

import de.vptr.midas.api.rest.entity.UserEntity;
import de.vptr.midas.api.rest.entity.UserGroupEntity;
import de.vptr.midas.api.rest.entity.UserGroupMetaEntity;
import de.vptr.midas.api.rest.service.UserGroupService;
import de.vptr.midas.api.rest.util.ResponseUtil;
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
    public List<UserGroupEntity> getAllGroups() {
        return this.groupService.getAllGroups();
    }

    @GET
    @Path("/{id}")
    public Response getGroup(@PathParam("id") final Long id) {
        return ResponseUtil.okOrNotFound(this.groupService.findById(id));
    }

    @GET
    @Path("/name/{name}")
    public Response getGroupByName(@PathParam("name") final String name) {
        return ResponseUtil.okOrNotFound(this.groupService.findByName(name));
    }

    @GET
    @Path("/{id}/users")
    public List<UserEntity> getUsersInGroup(@PathParam("id") final Long groupId) {
        return this.groupService.getUsersInGroup(groupId);
    }

    @GET
    @Path("/user/{userId}")
    public List<UserGroupEntity> getGroupsForUser(@PathParam("userId") final Long userId) {
        return this.groupService.getGroupsForUser(userId);
    }

    @GET
    @Path("/user/{userId}/group/{groupId}/check")
    public Response checkUserInGroup(@PathParam("userId") final Long userId, @PathParam("groupId") final Long groupId) {
        final boolean isInGroup = this.groupService.isUserInGroup(userId, groupId);
        return ResponseUtil.ok(isInGroup);
    }

    @POST
    @RolesAllowed({ "user-group:add" })
    public Response createGroup(final UserGroupEntity group) {
        final UserGroupEntity created = this.groupService.createGroup(group);
        return ResponseUtil.created(created);
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "user-group:edit" })
    public Response updateGroup(@PathParam("id") final Long id, final UserGroupEntity group) {
        group.id = id;
        final UserGroupEntity updated = this.groupService.updateGroup(group);
        return ResponseUtil.ok(updated);
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "user-group:edit" })
    public Response patchGroup(@PathParam("id") final Long id, final UserGroupEntity group) {
        group.id = id;
        final UserGroupEntity updated = this.groupService.patchGroup(group);
        return ResponseUtil.ok(updated);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "user-group:delete" })
    public Response deleteGroup(@PathParam("id") final Long id) {
        final boolean deleted = this.groupService.deleteGroup(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return ResponseUtil.notFound();
    }

    @POST
    @Path("/{groupId}/users/{userId}")
    @RolesAllowed({ "user-group:edit" })
    public Response addUserToGroup(@PathParam("groupId") final Long groupId, @PathParam("userId") final Long userId) {
        final UserGroupMetaEntity meta = this.groupService.addUserToGroup(userId, groupId);
        return ResponseUtil.created(meta);
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
        return ResponseUtil.notFound();
    }
}
