package de.vptr.midas.api.rest.resource;

import java.util.List;
import java.util.stream.Collectors;

import de.vptr.midas.api.rest.dto.UserDto;
import de.vptr.midas.api.rest.dto.UserResponseDto;
import de.vptr.midas.api.rest.service.UserService;
import de.vptr.midas.api.rest.util.ResponseUtil;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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
    public List<UserResponseDto> getAllUsers() {
        return this.userService.getAllUsers()
                .stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/me")
    public Response getCurrentUser() {
        final String username = this.securityContext.getUserPrincipal().getName();
        return ResponseUtil.okOrNotFound(
                this.userService.findByUsername(username).map(UserResponseDto::new));
    }

    @POST
    @RolesAllowed({ "user:add" })
    public Response createUser(@Valid final UserDto userDto) {
        final UserResponseDto created = this.userService.createUser(userDto);
        return ResponseUtil.created(created);
    }

    @GET
    @Path("/username/{username}")
    @RolesAllowed({ "user:delete", "user:edit" })
    public Response getUserByUsername(@PathParam("username") final String username) {
        return ResponseUtil.okOrNotFound(
                this.userService.findByUsername(username).map(UserResponseDto::new));
    }

    @GET
    @Path("/email/{email}")
    @RolesAllowed({ "user:delete", "user:edit" })
    public Response getUserByEmail(@PathParam("email") final String email) {
        return ResponseUtil.okOrNotFound(
                this.userService.findByEmail(email).map(UserResponseDto::new));
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "user:delete", "user:edit" })
    public Response getUser(@PathParam("id") final Long id) {
        return ResponseUtil.okOrNotFound(
                this.userService.findById(id).map(UserResponseDto::new));
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "user:edit" })
    public Response updateUser(@PathParam("id") final Long id, @Valid final UserDto userDto) {
        final UserResponseDto updated = this.userService.updateUser(id, userDto);
        return ResponseUtil.ok(updated);
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "user:edit" })
    public Response patchUser(@PathParam("id") final Long id, final UserDto userDto) {
        final UserResponseDto updated = this.userService.patchUser(id, userDto);
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
