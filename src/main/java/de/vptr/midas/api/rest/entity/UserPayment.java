package de.vptr.midas.api.rest.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "user_payments")
public class UserPayment extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    public UserAccount targetAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    public UserAccount sourceAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User userId;

    @NotBlank
    public String comment;

    @NotNull
    public LocalDate date;

    @NotNull
    @DecimalMin(value = "0.00", message = "Amount must be positive")
    @Column(precision = 15, scale = 2)
    public BigDecimal amount;

    public LocalDateTime created;

    @Column(name = "last_edit")
    public LocalDateTime lastEdit;

    // Helper methods for queries
    public static List<UserPayment> findByUserId(final Long userId) {
        return find("user.id", userId).list();
    }

    public static List<UserPayment> findBySourceAccountId(final Long sourceId) {
        return find("sourceAccount.id", sourceId).list();
    }

    public static List<UserPayment> findByTargetAccountId(final Long targetId) {
        return find("targetAccount.id", targetId).list();
    }

    public static List<UserPayment> findByDateRange(final LocalDate startDate, final LocalDate endDate) {
        return find("date BETWEEN ?1 AND ?2", startDate, endDate).list();
    }

    public static List<UserPayment> findRecentPayments(final int limit) {
        return find("ORDER BY created DESC").page(0, limit).list();
    }

    // Helper method to get payments by amount range
    public static List<UserPayment> findByAmountRange(final BigDecimal minAmount, final BigDecimal maxAmount) {
        return find("amount BETWEEN ?1 AND ?2", minAmount, maxAmount).list();
    }

    // Helper method to calculate total amount for a user
    public static BigDecimal getTotalAmountByUser(final Long userId) {
        return find("SELECT SUM(p.amount) FROM UserPayment p WHERE p.user.id = ?1", userId)
                .project(BigDecimal.class)
                .firstResult();
    }
}
