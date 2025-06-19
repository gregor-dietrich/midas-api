package de.vptr.midas.api.rest.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.entity.UserPayment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class UserPaymentService {

    public List<UserPayment> getAllPayments() {
        return UserPayment.listAll();
    }

    public Optional<UserPayment> findById(final Long id) {
        return UserPayment.findByIdOptional(id);
    }

    public List<UserPayment> findByUserId(final Long userId) {
        return UserPayment.findByUserId(userId);
    }

    public List<UserPayment> findBySourceAccountId(final Long sourceId) {
        return UserPayment.findBySourceAccountId(sourceId);
    }

    public List<UserPayment> findByTargetAccountId(final Long targetId) {
        return UserPayment.findByTargetAccountId(targetId);
    }

    public List<UserPayment> findByDateRange(final LocalDate startDate, final LocalDate endDate) {
        return UserPayment.findByDateRange(startDate, endDate);
    }

    public List<UserPayment> findRecentPayments(final int limit) {
        return UserPayment.findRecentPayments(limit);
    }

    public List<UserPayment> findByAmountRange(final BigDecimal minAmount, final BigDecimal maxAmount) {
        return UserPayment.findByAmountRange(minAmount, maxAmount);
    }

    public BigDecimal getTotalAmountByUser(final Long userId) {
        final var total = UserPayment.getTotalAmountByUser(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional
    public UserPayment createPayment(final UserPayment payment) {
        payment.created = LocalDateTime.now();
        payment.lastEdit = payment.created;

        payment.persist();
        return payment;
    }

    @Transactional
    public UserPayment updatePayment(final UserPayment payment) {
        final UserPayment existingPayment = UserPayment.findById(payment.id);
        if (existingPayment == null) {
            throw new WebApplicationException("Payment not found", Response.Status.NOT_FOUND);
        }

        // Complete replacement (PUT semantics)
        existingPayment.targetAccount = payment.targetAccount;
        existingPayment.sourceAccount = payment.sourceAccount;
        existingPayment.user = payment.user;
        existingPayment.comment = payment.comment;
        existingPayment.date = payment.date;
        existingPayment.amount = payment.amount;
        existingPayment.lastEdit = LocalDateTime.now();

        existingPayment.persist();
        return existingPayment;
    }

    @Transactional
    public UserPayment patchPayment(final UserPayment payment) {
        final UserPayment existingPayment = UserPayment.findById(payment.id);
        if (existingPayment == null) {
            throw new WebApplicationException("Payment not found", Response.Status.NOT_FOUND);
        }

        // Partial update (PATCH semantics) - only update provided fields
        if (payment.targetAccount != null) {
            existingPayment.targetAccount = payment.targetAccount;
        }
        if (payment.sourceAccount != null) {
            existingPayment.sourceAccount = payment.sourceAccount;
        }
        if (payment.user != null) {
            existingPayment.user = payment.user;
        }
        if (payment.comment != null) {
            existingPayment.comment = payment.comment;
        }
        if (payment.date != null) {
            existingPayment.date = payment.date;
        }
        if (payment.amount != null) {
            existingPayment.amount = payment.amount;
        }

        existingPayment.lastEdit = LocalDateTime.now();
        existingPayment.persist();
        return existingPayment;
    }

    @Transactional
    public boolean deletePayment(final Long id) {
        return UserPayment.deleteById(id);
    }
}
