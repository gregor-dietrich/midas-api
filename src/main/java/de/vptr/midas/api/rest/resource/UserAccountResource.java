package de.vptr.midas.api.rest.resource;

import java.math.BigDecimal;
import java.util.List;

import de.vptr.midas.api.rest.dto.UserAccountDto;
import de.vptr.midas.api.rest.entity.UserAccountEntity;
import de.vptr.midas.api.rest.entity.UserAccountMetaEntity;
import de.vptr.midas.api.rest.entity.UserEntity;
import de.vptr.midas.api.rest.entity.UserPaymentEntity;
import de.vptr.midas.api.rest.service.UserAccountService;
import de.vptr.midas.api.rest.util.ResponseUtil;
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
    public List<UserAccountEntity> getAllAccounts() {
        return this.accountService.getAllAccounts();
    }

    @GET
    @Path("/{id}")
    public Response getAccount(@PathParam("id") final Long id) {
        return ResponseUtil.okOrNotFound(this.accountService.findById(id));
    }

    @GET
    @Path("/name/{name}")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public Response getAccountByName(@PathParam("name") final String name) {
        return ResponseUtil.okOrNotFound(this.accountService.findByName(name));
    }

    @GET
    @Path("/{id}/users")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<UserEntity> getAssociatedUsers(@PathParam("id") final Long accountId) {
        return this.accountService.getAssociatedUsers(accountId);
    }

    @GET
    @Path("/{id}/payments")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<UserPaymentEntity> getAccountPayments(@PathParam("id") final Long accountId) {
        return this.accountService.getAccountPayments(accountId);
    }

    @GET
    @Path("/{id}/payments/outgoing")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<UserPaymentEntity> getOutgoingPayments(@PathParam("id") final Long accountId) {
        return this.accountService.getOutgoingPayments(accountId);
    }

    @GET
    @Path("/{id}/payments/incoming")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<UserPaymentEntity> getIncomingPayments(@PathParam("id") final Long accountId) {
        return this.accountService.getIncomingPayments(accountId);
    }

    @GET
    @Path("/{id}/balance")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public Response getAccountBalance(@PathParam("id") final Long accountId) {
        final BigDecimal balance = this.accountService.getAccountBalance(accountId);
        return ResponseUtil.ok("{\"balance\": " + balance + "}");
    }

    @GET
    @Path("/user/{userId}/account/{accountId}/check")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public Response checkUserAccountAssociation(@PathParam("userId") final Long userId,
            @PathParam("accountId") final Long accountId) {
        final boolean isAssociated = this.accountService.isUserAssociatedWithAccount(userId, accountId);
        return ResponseUtil.ok("{\"associated\": " + isAssociated + "}");
    }

    @POST
    @RolesAllowed({ "user_account:add" })
    public Response createAccount(final UserAccountEntity account) {
        final UserAccountEntity created = this.accountService.createAccount(account);
        return ResponseUtil.created(UserAccountDto.fromEntity(created));
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "user-group:edit" })
    public Response updateAccount(@PathParam("id") final Long id, final UserAccountEntity account) {
        account.id = id;
        final UserAccountEntity updated = this.accountService.updateAccount(account);
        return ResponseUtil.ok(UserAccountDto.fromEntity(updated));
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "user-group:edit" })
    public Response patchAccount(@PathParam("id") final Long id, final UserAccountEntity account) {
        account.id = id;
        final UserAccountEntity updated = this.accountService.patchAccount(account);
        return ResponseUtil.ok(UserAccountDto.fromEntity(updated));
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "user_account:delete" })
    public Response deleteAccount(@PathParam("id") final Long id) {
        final boolean deleted = this.accountService.deleteAccount(id);
        if (deleted) {
            return ResponseUtil.noContent();
        }
        return ResponseUtil.notFound();
    }

    @POST
    @Path("/{accountId}/users/{userId}")
    @RolesAllowed({ "user-group:edit" })
    public Response associateUserWithAccount(@PathParam("accountId") final Long accountId,
            @PathParam("userId") final Long userId) {
        final UserAccountMetaEntity meta = this.accountService.associateUserWithAccount(userId, accountId);
        return ResponseUtil.created(meta);
    }

    @DELETE
    @Path("/{accountId}/users/{userId}")
    @RolesAllowed({ "user-group:edit" })
    public Response removeUserFromAccount(@PathParam("accountId") final Long accountId,
            @PathParam("userId") final Long userId) {
        final boolean removed = this.accountService.removeUserFromAccount(userId, accountId);
        if (removed) {
            return ResponseUtil.noContent();
        }
        return ResponseUtil.notFound();
    }

    @GET
    @Path("/search")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<UserAccountEntity> searchAccounts(@QueryParam("query") final String query) {
        return this.accountService.searchAccounts(query);
    }
}
