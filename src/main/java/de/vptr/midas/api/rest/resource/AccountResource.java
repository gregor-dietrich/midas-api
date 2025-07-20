package de.vptr.midas.api.rest.resource;

import java.math.BigDecimal;
import java.util.List;

import de.vptr.midas.api.rest.dto.AccountDto;
import de.vptr.midas.api.rest.dto.AccountResponseDto;
import de.vptr.midas.api.rest.entity.AccountEntity;
import de.vptr.midas.api.rest.entity.PaymentEntity;
import de.vptr.midas.api.rest.entity.UserAccountMetaEntity;
import de.vptr.midas.api.rest.entity.UserEntity;
import de.vptr.midas.api.rest.service.AccountService;
import de.vptr.midas.api.rest.util.ResponseUtil;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class AccountResource {

    @Inject
    AccountService accountService;

    @GET
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<AccountEntity> getAllAccounts() {
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
    public List<PaymentEntity> getAccountPayments(@PathParam("id") final Long accountId) {
        return this.accountService.getAccountPayments(accountId);
    }

    @GET
    @Path("/{id}/payments/outgoing")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<PaymentEntity> getOutgoingPayments(@PathParam("id") final Long accountId) {
        return this.accountService.getOutgoingPayments(accountId);
    }

    @GET
    @Path("/{id}/payments/incoming")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<PaymentEntity> getIncomingPayments(@PathParam("id") final Long accountId) {
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
    @RolesAllowed({ "user-account:add" })
    public Response createAccount(@Valid final AccountDto accountDto) {
        // Map DTO to entity
        final AccountEntity account = new AccountEntity();
        account.name = accountDto.name;

        final AccountEntity created = this.accountService.createAccount(account);
        final AccountResponseDto responseDto = new AccountResponseDto(created);
        return ResponseUtil.created(responseDto);
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "user-group:edit" })
    public Response updateAccount(@PathParam("id") final Long id, @Valid final AccountDto accountDto) {
        // Map DTO to entity
        final AccountEntity account = new AccountEntity();
        account.id = id;
        account.name = accountDto.name;

        final AccountEntity updated = this.accountService.updateAccount(account);
        final AccountResponseDto responseDto = new AccountResponseDto(updated);
        return ResponseUtil.ok(responseDto);
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "user-group:edit" })
    public Response patchAccount(@PathParam("id") final Long id, @Valid final AccountDto accountDto) {
        // Map DTO to entity
        final AccountEntity account = new AccountEntity();
        account.id = id;
        account.name = accountDto.name;

        final AccountEntity updated = this.accountService.patchAccount(account);
        final AccountResponseDto responseDto = new AccountResponseDto(updated);
        return ResponseUtil.ok(responseDto);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "user-account:delete" })
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
    public List<AccountEntity> searchAccounts(@QueryParam("query") final String query) {
        return this.accountService.searchAccounts(query);
    }
}
