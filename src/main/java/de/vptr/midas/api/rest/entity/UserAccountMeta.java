package de.vptr.midas.api.rest.entity;

import java.time.LocalDateTime;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "user_accounts_meta")
public class UserAccountMeta extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    public UserAccount account;

    public LocalDateTime timestamp;

    // Helper method to find by user and account
    public static UserAccountMeta findByUserAndAccount(final Long userId, final Long accountId) {
        return find("user.id = ?1 AND account.id = ?2", userId, accountId).firstResult();
    }

    // Helper method to check if association exists
    public static boolean existsByUserAndAccount(final Long userId, final Long accountId) {
        return count("user.id = ?1 AND account.id = ?2", userId, accountId) > 0;
    }

    // Helper method to find all accounts for a user
    public static List<UserAccountMeta> findByUserId(final Long userId) {
        return find("user.id", userId).list();
    }

    // Helper method to find all users for an account
    public static List<UserAccountMeta> findByAccountId(final Long accountId) {
        return find("account.id", accountId).list();
    }
}
