package de.vptr.midas.api.rest.resource;

import java.util.List;

import de.vptr.midas.api.rest.entity.UserEntity;
import de.vptr.midas.api.rest.service.UserService;
import de.vptr.midas.api.rest.util.ResponseUtil;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class UserResource {

    @Inject
    UserService userService;

    @Context
    SecurityContext securityContext;

    @GET
    @RolesAllowed({ "user:delete", "user:edit" })
    public List<UserEntity> getAllUsers() {
        return this.userService.getAllUsers();
    }

    @GET
    @Path("/me")
    public Response getCurrentUser() {
        final String username = this.securityContext.getUserPrincipal().getName();
        return ResponseUtil.okOrNotFound(this.userService.findByUsername(username));
    }

    @POST
    @RolesAllowed({ "user:add" })
    public Response createUser(final UserEntity user) {
        final UserEntity created = this.userService.createUser(user);
        return ResponseUtil.created(created);
    }

    @GET
    @Path("/username/{username}")
    @RolesAllowed({ "user:delete", "user:edit" })
    public Response getUserByUsername(@PathParam("username") final String username) {
        return ResponseUtil.okOrNotFound(this.userService.findByUsername(username));
    }

    @GET
    @Path("/email/{email}")
    @RolesAllowed({ "user:delete", "user:edit" })
    public Response getUserByEmail(@PathParam("email") final String email) {
        return ResponseUtil.okOrNotFound(this.userService.findByEmail(email));
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "user:delete", "user:edit" })
    public Response getUser(@PathParam("id") final Long id) {
        return ResponseUtil.okOrNotFound(UserEntity.findByIdOptional(id));
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "user:edit" })
    public Response updateUser(@PathParam("id") final Long id, final UserEntity user) {
        user.id = id;
        final UserEntity updated = this.userService.updateUser(user);
        return ResponseUtil.ok(updated);
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "user:edit" })
    public Response patchUser(@PathParam("id") final Long id, final UserEntity user) {
        user.id = id;
        final UserEntity updated = this.userService.patchUser(user);
        return ResponseUtil.ok(updated);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "user:delete" })
    public Response deleteUser(@PathParam("id") final Long id) {
        final boolean deleted = this.userService.deleteUser(id);
        if (deleted) {
            return ResponseUtil.noContent();
        }
        return ResponseUtil.notFound();
    }
}
