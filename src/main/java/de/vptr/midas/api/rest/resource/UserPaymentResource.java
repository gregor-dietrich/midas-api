package de.vptr.midas.api.rest.resource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import de.vptr.midas.api.rest.entity.UserPayment;
import de.vptr.midas.api.rest.service.UserPaymentService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/user-payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class UserPaymentResource {

    @Inject
    UserPaymentService paymentService;

    @GET
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<UserPayment> getAllPayments() {
        return this.paymentService.getAllPayments();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public Response getPayment(@PathParam("id") final Long id) {
        return this.paymentService.findById(id)
                .map(payment -> Response.ok(payment).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/user/{userId}")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<UserPayment> getPaymentsByUser(@PathParam("userId") final Long userId) {
        return this.paymentService.findByUserId(userId);
    }

    @GET
    @Path("/source/{sourceId}")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<UserPayment> getPaymentsBySourceAccount(@PathParam("sourceId") final Long sourceId) {
        return this.paymentService.findBySourceAccountId(sourceId);
    }

    @GET
    @Path("/target/{targetId}")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<UserPayment> getPaymentsByTargetAccount(@PathParam("targetId") final Long targetId) {
        return this.paymentService.findByTargetAccountId(targetId);
    }

    @GET
    @Path("/date-range")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<UserPayment> getPaymentsByDateRange(
            @QueryParam("startDate") final LocalDate startDate,
            @QueryParam("endDate") final LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException("Both startDate and endDate parameters are required");
        }
        return this.paymentService.findByDateRange(startDate, endDate);
    }

    @GET
    @Path("/recent")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<UserPayment> getRecentPayments(@QueryParam("limit") @DefaultValue("10") final int limit) {
        return this.paymentService.findRecentPayments(limit);
    }

    @GET
    @Path("/amount-range")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public List<UserPayment> getPaymentsByAmountRange(
            @QueryParam("minAmount") final BigDecimal minAmount,
            @QueryParam("maxAmount") final BigDecimal maxAmount) {
        if (minAmount == null || maxAmount == null) {
            throw new BadRequestException("Both minAmount and maxAmount parameters are required");
        }
        return this.paymentService.findByAmountRange(minAmount, maxAmount);
    }

    @GET
    @Path("/user/{userId}/total")
    @RolesAllowed({ "user-group:edit", "user-group:delete" })
    public Response getTotalAmountByUser(@PathParam("userId") final Long userId) {
        final BigDecimal total = this.paymentService.getTotalAmountByUser(userId);
        return Response.ok(total).build();
    }

    @POST
    @RolesAllowed({ "user-group:add" })
    public Response createPayment(final UserPayment payment) {
        final UserPayment created = this.paymentService.createPayment(payment);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "user-group:edit" })
    public Response updatePayment(@PathParam("id") final Long id, final UserPayment payment) {
        payment.id = id;
        final UserPayment updated = this.paymentService.updatePayment(payment);
        return Response.ok(updated).build();
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "user-group:edit" })
    public Response patchPayment(@PathParam("id") final Long id, final UserPayment payment) {
        payment.id = id;
        final UserPayment updated = this.paymentService.patchPayment(payment);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "user-group:delete" })
    public Response deletePayment(@PathParam("id") final Long id) {
        final boolean deleted = this.paymentService.deletePayment(id);
        if (deleted) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
