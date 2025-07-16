package de.vptr.midas.api.rest.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.entity.UserPaymentEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class UserPaymentService {

    public List<UserPaymentEntity> getAllPayments() {
        return UserPaymentEntity.listAll();
    }

    public Optional<UserPaymentEntity> findById(final Long id) {
        return UserPaymentEntity.findByIdOptional(id);
    }

    public List<UserPaymentEntity> findByUserId(final Long userId) {
        return UserPaymentEntity.findByUserId(userId);
    }

    public List<UserPaymentEntity> findBySourceAccountId(final Long sourceId) {
        return UserPaymentEntity.findBySourceAccountId(sourceId);
    }

    public List<UserPaymentEntity> findByTargetAccountId(final Long targetId) {
        return UserPaymentEntity.findByTargetAccountId(targetId);
    }

    public List<UserPaymentEntity> findByDateRange(final LocalDate startDate, final LocalDate endDate) {
        return UserPaymentEntity.findByDateRange(startDate, endDate);
    }

    public List<UserPaymentEntity> findRecentPayments(final int limit) {
        return UserPaymentEntity.findRecentPayments(limit);
    }

    public List<UserPaymentEntity> findByAmountRange(final BigDecimal minAmount, final BigDecimal maxAmount) {
        return UserPaymentEntity.findByAmountRange(minAmount, maxAmount);
    }

    public BigDecimal getTotalAmountByUser(final Long userId) {
        final var total = UserPaymentEntity.getTotalAmountByUser(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional
    public UserPaymentEntity createPayment(final UserPaymentEntity payment) {
        payment.created = LocalDateTime.now();
        payment.lastEdit = payment.created;

        payment.persist();
        return payment;
    }

    @Transactional
    public UserPaymentEntity updatePayment(final UserPaymentEntity payment) {
        final UserPaymentEntity existingPayment = UserPaymentEntity.findById(payment.id);
        if (existingPayment == null) {
            throw new WebApplicationException("Payment not found", Response.Status.NOT_FOUND);
        }

        // Complete replacement (PUT semantics)
        existingPayment.targetAccount = payment.targetAccount;
        existingPayment.sourceAccount = payment.sourceAccount;
        existingPayment.userId = payment.userId;
        existingPayment.comment = payment.comment;
        existingPayment.date = payment.date;
        existingPayment.amount = payment.amount;
        existingPayment.lastEdit = LocalDateTime.now();

        existingPayment.persist();
        return existingPayment;
    }

    @Transactional
    public UserPaymentEntity patchPayment(final UserPaymentEntity payment) {
        final UserPaymentEntity existingPayment = UserPaymentEntity.findById(payment.id);
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
        if (payment.userId != null) {
            existingPayment.userId = payment.userId;
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
        return UserPaymentEntity.deleteById(id);
    }
}
