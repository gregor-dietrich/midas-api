package de.vptr.midas.api.rest.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.dto.PaymentDto;
import de.vptr.midas.api.rest.dto.PaymentResponseDto;
import de.vptr.midas.api.rest.entity.AccountEntity;
import de.vptr.midas.api.rest.entity.PaymentEntity;
import de.vptr.midas.api.rest.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class PaymentService {

    public List<PaymentResponseDto> getAllPayments() {
        return PaymentEntity.listAll().stream()
                .map(entity -> new PaymentResponseDto((PaymentEntity) entity))
                .toList();
    }

    public Optional<PaymentResponseDto> findById(final Long id) {
        return PaymentEntity.findByIdOptional(id)
                .map(entity -> new PaymentResponseDto((PaymentEntity) entity));
    }

    public List<PaymentResponseDto> findByUserId(final Long userId) {
        return PaymentEntity.findByUserId(userId).stream()
                .map(PaymentResponseDto::new)
                .toList();
    }

    public List<PaymentResponseDto> findBySourceAccountId(final Long sourceId) {
        return PaymentEntity.findBySourceAccountId(sourceId).stream()
                .map(PaymentResponseDto::new)
                .toList();
    }

    public List<PaymentResponseDto> findByTargetAccountId(final Long targetId) {
        return PaymentEntity.findByTargetAccountId(targetId).stream()
                .map(PaymentResponseDto::new)
                .toList();
    }

    public List<PaymentResponseDto> findByDateRange(final LocalDate startDate, final LocalDate endDate) {
        return PaymentEntity.findByDateRange(startDate, endDate).stream()
                .map(PaymentResponseDto::new)
                .toList();
    }

    public List<PaymentResponseDto> findRecentPayments(final int limit) {
        return PaymentEntity.findRecentPayments(limit).stream()
                .map(PaymentResponseDto::new)
                .toList();
    }

    public List<PaymentResponseDto> findByAmountRange(final BigDecimal minAmount, final BigDecimal maxAmount) {
        return PaymentEntity.findByAmountRange(minAmount, maxAmount).stream()
                .map(PaymentResponseDto::new)
                .toList();
    }

    public BigDecimal getTotalAmountByUser(final Long userId) {
        final var total = PaymentEntity.getTotalAmountByUser(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional
    public PaymentResponseDto createPayment(final PaymentDto paymentDto) {
        // Validate required fields
        if (paymentDto.targetAccountId == null) {
            throw new ValidationException("Target account ID is required");
        }
        if (paymentDto.sourceAccountId == null) {
            throw new ValidationException("Source account ID is required");
        }
        if (paymentDto.userId == null) {
            throw new ValidationException("User ID is required");
        }
        if (paymentDto.comment == null || paymentDto.comment.trim().isEmpty()) {
            throw new ValidationException("Comment is required");
        }
        if (paymentDto.date == null) {
            throw new ValidationException("Date is required");
        }
        if (paymentDto.amount == null || paymentDto.amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be positive");
        }

        // Find referenced entities
        final AccountEntity targetAccount = AccountEntity.findById(paymentDto.targetAccountId);
        if (targetAccount == null) {
            throw new WebApplicationException("Target account not found", Response.Status.BAD_REQUEST);
        }

        final AccountEntity sourceAccount = AccountEntity.findById(paymentDto.sourceAccountId);
        if (sourceAccount == null) {
            throw new WebApplicationException("Source account not found", Response.Status.BAD_REQUEST);
        }

        final UserEntity user = UserEntity.findById(paymentDto.userId);
        if (user == null) {
            throw new WebApplicationException("User not found", Response.Status.BAD_REQUEST);
        }

        final PaymentEntity payment = new PaymentEntity();
        payment.targetAccount = targetAccount;
        payment.sourceAccount = sourceAccount;
        payment.userId = user;
        payment.comment = paymentDto.comment;
        payment.date = paymentDto.date;
        payment.amount = paymentDto.amount;
        payment.created = LocalDateTime.now();
        payment.lastEdit = payment.created;

        payment.persist();
        return new PaymentResponseDto(payment);
    }

    @Transactional
    public PaymentResponseDto updatePayment(final Long id, final PaymentDto paymentDto) {
        // Validate required fields
        if (paymentDto.targetAccountId == null) {
            throw new ValidationException("Target account ID is required");
        }
        if (paymentDto.sourceAccountId == null) {
            throw new ValidationException("Source account ID is required");
        }
        if (paymentDto.userId == null) {
            throw new ValidationException("User ID is required");
        }
        if (paymentDto.comment == null || paymentDto.comment.trim().isEmpty()) {
            throw new ValidationException("Comment is required");
        }
        if (paymentDto.date == null) {
            throw new ValidationException("Date is required");
        }
        if (paymentDto.amount == null || paymentDto.amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be positive");
        }

        final PaymentEntity existingPayment = PaymentEntity.findById(id);
        if (existingPayment == null) {
            throw new WebApplicationException("Payment not found", Response.Status.NOT_FOUND);
        }

        // Find referenced entities
        final AccountEntity targetAccount = AccountEntity.findById(paymentDto.targetAccountId);
        if (targetAccount == null) {
            throw new WebApplicationException("Target account not found", Response.Status.BAD_REQUEST);
        }

        final AccountEntity sourceAccount = AccountEntity.findById(paymentDto.sourceAccountId);
        if (sourceAccount == null) {
            throw new WebApplicationException("Source account not found", Response.Status.BAD_REQUEST);
        }

        final UserEntity user = UserEntity.findById(paymentDto.userId);
        if (user == null) {
            throw new WebApplicationException("User not found", Response.Status.BAD_REQUEST);
        }

        // Complete replacement (PUT semantics)
        existingPayment.targetAccount = targetAccount;
        existingPayment.sourceAccount = sourceAccount;
        existingPayment.userId = user;
        existingPayment.comment = paymentDto.comment;
        existingPayment.date = paymentDto.date;
        existingPayment.amount = paymentDto.amount;
        existingPayment.lastEdit = LocalDateTime.now();

        existingPayment.persist();
        return new PaymentResponseDto(existingPayment);
    }

    @Transactional
    public PaymentResponseDto patchPayment(final Long id, final PaymentDto paymentDto) {
        final PaymentEntity existingPayment = PaymentEntity.findById(id);
        if (existingPayment == null) {
            throw new WebApplicationException("Payment not found", Response.Status.NOT_FOUND);
        }

        // Partial update (PATCH semantics) - only update provided fields
        if (paymentDto.targetAccountId != null) {
            final AccountEntity targetAccount = AccountEntity.findById(paymentDto.targetAccountId);
            if (targetAccount == null) {
                throw new WebApplicationException("Target account not found", Response.Status.BAD_REQUEST);
            }
            existingPayment.targetAccount = targetAccount;
        }
        if (paymentDto.sourceAccountId != null) {
            final AccountEntity sourceAccount = AccountEntity.findById(paymentDto.sourceAccountId);
            if (sourceAccount == null) {
                throw new WebApplicationException("Source account not found", Response.Status.BAD_REQUEST);
            }
            existingPayment.sourceAccount = sourceAccount;
        }
        if (paymentDto.userId != null) {
            final UserEntity user = UserEntity.findById(paymentDto.userId);
            if (user == null) {
                throw new WebApplicationException("User not found", Response.Status.BAD_REQUEST);
            }
            existingPayment.userId = user;
        }
        if (paymentDto.comment != null && !paymentDto.comment.trim().isEmpty()) {
            existingPayment.comment = paymentDto.comment;
        }
        if (paymentDto.date != null) {
            existingPayment.date = paymentDto.date;
        }
        if (paymentDto.amount != null && paymentDto.amount.compareTo(BigDecimal.ZERO) > 0) {
            existingPayment.amount = paymentDto.amount;
        }

        existingPayment.lastEdit = LocalDateTime.now();
        existingPayment.persist();
        return new PaymentResponseDto(existingPayment);
    }

    @Transactional
    public boolean deletePayment(final Long id) {
        return PaymentEntity.deleteById(id);
    }
}
