package de.vptr.midas.api.rest.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.entity.UserAccountEntity;
import de.vptr.midas.api.rest.entity.UserAccountMetaEntity;
import de.vptr.midas.api.rest.entity.UserEntity;
import de.vptr.midas.api.rest.entity.UserPaymentEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class UserAccountService {

    public List<UserAccountEntity> getAllAccounts() {
        return UserAccountEntity.listAll();
    }

    public Optional<UserAccountEntity> findById(final Long id) {
        return UserAccountEntity.findByIdOptional(id);
    }

    public Optional<UserAccountEntity> findByName(final String name) {
        return Optional.ofNullable(UserAccountEntity.findByName(name));
    }

    public List<UserEntity> getAssociatedUsers(final Long accountId) {
        final UserAccountEntity account = UserAccountEntity.findById(accountId);
        if (account == null) {
            throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);
        }
        return account.getAssociatedUsers();
    }

    public List<UserAccountEntity> getAccountsForUser(final Long userId) {
        final List<UserAccountMetaEntity> metas = UserAccountMetaEntity.find("user.id", userId).list();
        return metas.stream()
                .map(meta -> meta.account)
                .toList();
    }

    public List<UserPaymentEntity> getAccountPayments(final Long accountId) {
        final var outgoing = UserPaymentEntity.findBySourceAccountId(accountId);
        final var incoming = UserPaymentEntity.findByTargetAccountId(accountId);

        outgoing.addAll(incoming);
        return outgoing.stream()
                .sorted((p1, p2) -> p2.created.compareTo(p1.created))
                .toList();
    }

    public List<UserPaymentEntity> getOutgoingPayments(final Long accountId) {
        return UserPaymentEntity.findBySourceAccountId(accountId);
    }

    public List<UserPaymentEntity> getIncomingPayments(final Long accountId) {
        return UserPaymentEntity.findByTargetAccountId(accountId);
    }

    public BigDecimal getAccountBalance(final Long accountId) {
        // Calculate balance as incoming - outgoing
        final var incoming = this.getIncomingPayments(accountId);
        final var outgoing = this.getOutgoingPayments(accountId);

        final BigDecimal incomingTotal = incoming.stream()
                .map(p -> p.amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal outgoingTotal = outgoing.stream()
                .map(p -> p.amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return incomingTotal.subtract(outgoingTotal);
    }

    @Transactional
    public UserAccountEntity createAccount(final UserAccountEntity account) {
        account.persist();
        return account;
    }

    @Transactional
    public UserAccountEntity updateAccount(final UserAccountEntity account) {
        final UserAccountEntity existingAccount = UserAccountEntity.findById(account.id);
        if (existingAccount == null) {
            throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);
        }

        // Complete replacement (PUT semantics)
        existingAccount.name = account.name;

        existingAccount.persist();
        return existingAccount;
    }

    @Transactional
    public UserAccountEntity patchAccount(final UserAccountEntity account) {
        final UserAccountEntity existingAccount = UserAccountEntity.findById(account.id);
        if (existingAccount == null) {
            throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);
        }

        // Partial update (PATCH semantics) - only update provided fields
        if (account.name != null) {
            existingAccount.name = account.name;
        }

        existingAccount.persist();
        return existingAccount;
    }

    @Transactional
    public boolean deleteAccount(final Long id) {
        final UserAccountEntity account = UserAccountEntity.findById(id);
        if (account == null) {
            return false;
        }

        // Check if account has any payments
        final var payments = this.getAccountPayments(id);
        if (!payments.isEmpty()) {
            throw new WebApplicationException("Cannot delete account with existing payments", Response.Status.CONFLICT);
        }

        return UserAccountEntity.deleteById(id);
    }

    @Transactional
    public UserAccountMetaEntity associateUserWithAccount(final Long userId, final Long accountId) {
        // Check if association already exists
        if (UserAccountMetaEntity.existsByUserAndAccount(userId, accountId)) {
            throw new WebApplicationException("User is already associated with this account", Response.Status.CONFLICT);
        }

        final UserEntity user = UserEntity.findById(userId);
        final UserAccountEntity account = UserAccountEntity.findById(accountId);

        if (user == null) {
            throw new WebApplicationException("User not found", Response.Status.NOT_FOUND);
        }
        if (account == null) {
            throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);
        }

        final UserAccountMetaEntity meta = new UserAccountMetaEntity();
        meta.user = user;
        meta.account = account;
        meta.timestamp = LocalDateTime.now();
        meta.persist();

        return meta;
    }

    @Transactional
    public boolean removeUserFromAccount(final Long userId, final Long accountId) {
        final var meta = UserAccountMetaEntity.findByUserAndAccount(userId, accountId);
        if (meta == null) {
            return false;
        }
        meta.delete();
        return true;
    }

    public boolean isUserAssociatedWithAccount(final Long userId, final Long accountId) {
        return UserAccountMetaEntity.existsByUserAndAccount(userId, accountId);
    }

    public List<UserAccountEntity> searchAccounts(final String query) {
        if (query == null || query.trim().isEmpty()) {
            return this.getAllAccounts();
        }

        final String searchTerm = "%" + query.trim().toLowerCase() + "%";

        // Search by account name or associated username
        return UserAccountEntity.find(
                "SELECT DISTINCT ua FROM UserAccount ua " +
                        "LEFT JOIN ua.userAccountMetas uam " +
                        "LEFT JOIN uam.user u " +
                        "WHERE LOWER(ua.name) LIKE ?1 OR LOWER(u.username) LIKE ?1",
                searchTerm).list();
    }
}
