package de.vptr.midas.api.rest.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.entity.PaymentEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class PaymentService {

    public List<PaymentEntity> getAllPayments() {
        return PaymentEntity.listAll();
    }

    public Optional<PaymentEntity> findById(final Long id) {
        return PaymentEntity.findByIdOptional(id);
    }

    public List<PaymentEntity> findByUserId(final Long userId) {
        return PaymentEntity.findByUserId(userId);
    }

    public List<PaymentEntity> findBySourceAccountId(final Long sourceId) {
        return PaymentEntity.findBySourceAccountId(sourceId);
    }

    public List<PaymentEntity> findByTargetAccountId(final Long targetId) {
        return PaymentEntity.findByTargetAccountId(targetId);
    }

    public List<PaymentEntity> findByDateRange(final LocalDate startDate, final LocalDate endDate) {
        return PaymentEntity.findByDateRange(startDate, endDate);
    }

    public List<PaymentEntity> findRecentPayments(final int limit) {
        return PaymentEntity.findRecentPayments(limit);
    }

    public List<PaymentEntity> findByAmountRange(final BigDecimal minAmount, final BigDecimal maxAmount) {
        return PaymentEntity.findByAmountRange(minAmount, maxAmount);
    }

    public BigDecimal getTotalAmountByUser(final Long userId) {
        final var total = PaymentEntity.getTotalAmountByUser(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional
    public PaymentEntity createPayment(final PaymentEntity payment) {
        payment.created = LocalDateTime.now();
        payment.lastEdit = payment.created;

        payment.persist();
        return payment;
    }

    @Transactional
    public PaymentEntity updatePayment(final PaymentEntity payment) {
        final PaymentEntity existingPayment = PaymentEntity.findById(payment.id);
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
    public PaymentEntity patchPayment(final PaymentEntity payment) {
        final PaymentEntity existingPayment = PaymentEntity.findById(payment.id);
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
        return PaymentEntity.deleteById(id);
    }
}
