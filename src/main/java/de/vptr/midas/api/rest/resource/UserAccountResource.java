package de.vptr.midas.api.rest.resource;

import java.math.BigDecimal;
import java.util.List;

import de.vptr.midas.api.rest.dto.UserAccountDto;
import de.vptr.midas.api.rest.entity.User;
import de.vptr.midas.api.rest.entity.UserAccount;
import de.vptr.midas.api.rest.entity.UserAccountMeta;
import de.vptr.midas.api.rest.entity.UserPayment;
import de.vptr.midas.api.rest.service.UserAccountService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/user-accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class UserAccountResource {

    @Inject
    UserAccountService accountService;

    @GET
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<UserAccount> getAllAccounts() {
        return this.accountService.getAllAccounts();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public Response getAccount(@PathParam("id") final Long id) {
        return this.accountService.findById(id)
                .map(account -> Response.ok(account).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/name/{name}")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public Response getAccountByName(@PathParam("name") final String name) {
        return this.accountService.findByName(name)
                .map(account -> Response.ok(account).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/{id}/users")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<User> getAssociatedUsers(@PathParam("id") final Long accountId) {
        return this.accountService.getAssociatedUsers(accountId);
    }

    @GET
    @Path("/user/{userId}")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<UserAccount> getAccountsForUser(@PathParam("userId") final Long userId) {
        return this.accountService.getAccountsForUser(userId);
    }

    @GET
    @Path("/{id}/payments")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<UserPayment> getAccountPayments(@PathParam("id") final Long accountId) {
        return this.accountService.getAccountPayments(accountId);
    }

    @GET
    @Path("/{id}/payments/outgoing")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<UserPayment> getOutgoingPayments(@PathParam("id") final Long accountId) {
        return this.accountService.getOutgoingPayments(accountId);
    }

    @GET
    @Path("/{id}/payments/incoming")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<UserPayment> getIncomingPayments(@PathParam("id") final Long accountId) {
        return this.accountService.getIncomingPayments(accountId);
    }

    @GET
    @Path("/{id}/balance")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public Response getAccountBalance(@PathParam("id") final Long accountId) {
        final BigDecimal balance = this.accountService.getAccountBalance(accountId);
        return Response.ok().entity("{\"balance\": " + balance + "}").build();
    }

    @GET
    @Path("/user/{userId}/account/{accountId}/check")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public Response checkUserAccountAssociation(@PathParam("userId") final Long userId,
            @PathParam("accountId") final Long accountId) {
        final boolean isAssociated = this.accountService.isUserAssociatedWithAccount(userId, accountId);
        return Response.ok().entity("{\"associated\": " + isAssociated + "}").build();
    }

    @POST
    @RolesAllowed({ "user-group:add" })
    public Response createAccount(final UserAccount account) {
        final UserAccount created = this.accountService.createAccount(account);
        return Response.status(Response.Status.CREATED).entity(UserAccountDto.fromEntity(created)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "user-group:edit" })
    public Response updateAccount(@PathParam("id") final Long id, final UserAccount account) {
        account.id = id;
        final UserAccount updated = this.accountService.updateAccount(account);
        return Response.ok(UserAccountDto.fromEntity(updated)).build();
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "user-group:edit" })
    public Response patchAccount(@PathParam("id") final Long id, final UserAccount account) {
        account.id = id;
        final UserAccount updated = this.accountService.patchAccount(account);
        return Response.ok(UserAccountDto.fromEntity(updated)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "user-group:delete" })
    public Response deleteAccount(@PathParam("id") final Long id) {
        final boolean deleted = this.accountService.deleteAccount(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/{accountId}/users/{userId}")
    @RolesAllowed({ "user-group:edit" })
    public Response associateUserWithAccount(@PathParam("accountId") final Long accountId,
            @PathParam("userId") final Long userId) {
        final UserAccountMeta meta = this.accountService.associateUserWithAccount(userId, accountId);
        return Response.status(Response.Status.CREATED).entity(meta).build();
    }

    @DELETE
    @Path("/{accountId}/users/{userId}")
    @RolesAllowed({ "user-group:edit" })
    public Response removeUserFromAccount(@PathParam("accountId") final Long accountId,
            @PathParam("userId") final Long userId) {
        final boolean removed = this.accountService.removeUserFromAccount(userId, accountId);
        if (removed) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/search")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<UserAccount> searchAccounts(@QueryParam("query") final String query) {
        return this.accountService.searchAccounts(query);
    }
}
