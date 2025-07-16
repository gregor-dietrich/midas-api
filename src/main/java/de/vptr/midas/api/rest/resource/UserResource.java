package de.vptr.midas.api.rest.resource;

import java.util.List;

import de.vptr.midas.api.rest.entity.UserEntity;
import de.vptr.midas.api.rest.service.UserService;
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
        return this.userService.findByUsername(username)
                .map(user -> Response.ok(user).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @RolesAllowed({ "user:add" })
    public Response createUser(final UserEntity user) {
        final UserEntity created = this.userService.createUser(user);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/username/{username}")
    @RolesAllowed({ "user:delete", "user:edit" })
    public Response getUserByUsername(@PathParam("username") final String username) {
        return this.userService.findByUsername(username)
                .map(user -> Response.ok(user).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/email/{email}")
    @RolesAllowed({ "user:delete", "user:edit" })
    public Response getUserByEmail(@PathParam("email") final String email) {
        return this.userService.findByEmail(email)
                .map(user -> Response.ok(user).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "user:delete", "user:edit" })
    public Response getUser(@PathParam("id") final Long id) {
        return UserEntity.findByIdOptional(id)
                .map(user -> Response.ok(user).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "user:edit" })
    public Response updateUser(@PathParam("id") final Long id, final UserEntity user) {
        user.id = id;
        final UserEntity updated = this.userService.updateUser(user);
        return Response.ok(updated).build();
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "user:edit" })
    public Response patchUser(@PathParam("id") final Long id, final UserEntity user) {
        user.id = id;
        final UserEntity updated = this.userService.patchUser(user);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "user:delete" })
    public Response deleteUser(@PathParam("id") final Long id) {
        final boolean deleted = this.userService.deleteUser(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
