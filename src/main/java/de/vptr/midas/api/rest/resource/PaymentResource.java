package de.vptr.midas.api.rest.resource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import de.vptr.midas.api.rest.dto.PaymentDto;
import de.vptr.midas.api.rest.dto.PaymentResponseDto;
import de.vptr.midas.api.rest.service.PaymentService;
import de.vptr.midas.api.rest.util.ResponseUtil;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class PaymentResource {

    @Inject
    PaymentService paymentService;

    @GET
    @RolesAllowed({ "user-account:edit", "user-account:delete" })
    public List<PaymentResponseDto> getAllPayments() {
        return this.paymentService.getAllPayments();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "user-account:edit", "user-account:delete" })
    public Response getPayment(@PathParam("id") final Long id) {
        return ResponseUtil.okOrNotFound(this.paymentService.findById(id));
    }

    @GET
    @Path("/user/{userId}")
    @RolesAllowed({ "user-account:edit", "user-account:delete" })
    public List<PaymentResponseDto> getPaymentsByUser(@PathParam("userId") final Long userId) {
        return this.paymentService.findByUserId(userId);
    }

    @GET
    @Path("/source/{sourceId}")
    @RolesAllowed({ "user-account:edit", "user-account:delete" })
    public List<PaymentResponseDto> getPaymentsBySourceAccount(@PathParam("sourceId") final Long sourceId) {
        return this.paymentService.findBySourceAccountId(sourceId);
    }

    @GET
    @Path("/target/{targetId}")
    @RolesAllowed({ "user-account:edit", "user-account:delete" })
    public List<PaymentResponseDto> getPaymentsByTargetAccount(@PathParam("targetId") final Long targetId) {
        return this.paymentService.findByTargetAccountId(targetId);
    }

    @GET
    @Path("/date-range")
    @RolesAllowed({ "user-account:edit", "user-account:delete" })
    public List<PaymentResponseDto> getPaymentsByDateRange(
            @QueryParam("startDate") final LocalDate startDate,
            @QueryParam("endDate") final LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException("Both startDate and endDate parameters are required");
        }
        return this.paymentService.findByDateRange(startDate, endDate);
    }

    @GET
    @Path("/recent")
    @RolesAllowed({ "user-account:edit", "user-account:delete" })
    public List<PaymentResponseDto> getRecentPayments(@QueryParam("limit") @DefaultValue("10") final int limit) {
        return this.paymentService.findRecentPayments(limit);
    }

    @GET
    @Path("/amount-range")
    @RolesAllowed({ "user-account:edit", "user-account:delete" })
    public List<PaymentResponseDto> getPaymentsByAmountRange(
            @QueryParam("minAmount") final BigDecimal minAmount,
            @QueryParam("maxAmount") final BigDecimal maxAmount) {
        if (minAmount == null || maxAmount == null) {
            throw new BadRequestException("Both minAmount and maxAmount parameters are required");
        }
        return this.paymentService.findByAmountRange(minAmount, maxAmount);
    }

    @GET
    @Path("/user/{userId}/total")
    @RolesAllowed({ "user-account:edit", "user-account:delete" })
    public Response getTotalAmountByUser(@PathParam("userId") final Long userId) {
        final BigDecimal total = this.paymentService.getTotalAmountByUser(userId);
        return ResponseUtil.ok(total);
    }

    @POST
    @RolesAllowed({ "user-account:add" })
    public Response createPayment(final PaymentDto paymentDto) {
        final PaymentResponseDto created = this.paymentService.createPayment(paymentDto);
        return ResponseUtil.created(created);
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "user-account:edit" })
    public Response updatePayment(@PathParam("id") final Long id, final PaymentDto paymentDto) {
        final PaymentResponseDto updated = this.paymentService.updatePayment(id, paymentDto);
        return ResponseUtil.ok(updated);
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "user-account:edit" })
    public Response patchPayment(@PathParam("id") final Long id, final PaymentDto paymentDto) {
        final PaymentResponseDto updated = this.paymentService.patchPayment(id, paymentDto);
        return ResponseUtil.ok(updated);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "user-account:delete" })
    public Response deletePayment(@PathParam("id") final Long id) {
        final boolean deleted = this.paymentService.deletePayment(id);
        if (deleted) {
            return ResponseUtil.noContent();
        }
        return ResponseUtil.notFound();
    }
}
