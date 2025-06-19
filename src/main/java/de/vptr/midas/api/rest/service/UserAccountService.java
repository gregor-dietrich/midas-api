package de.vptr.midas.api.rest.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.entity.User;
import de.vptr.midas.api.rest.entity.UserAccount;
import de.vptr.midas.api.rest.entity.UserAccountMeta;
import de.vptr.midas.api.rest.entity.UserPayment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class UserAccountService {

    public List<UserAccount> getAllAccounts() {
        return UserAccount.listAll();
    }

    public Optional<UserAccount> findById(final Long id) {
        return UserAccount.findByIdOptional(id);
    }

    public Optional<UserAccount> findByName(final String name) {
        return Optional.ofNullable(UserAccount.findByName(name));
    }

    public List<User> getAssociatedUsers(final Long accountId) {
        final UserAccount account = UserAccount.findById(accountId);
        if (account == null) {
            throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);
        }
        return account.getAssociatedUsers();
    }

    public List<UserAccount> getAccountsForUser(final Long userId) {
        final List<UserAccountMeta> metas = UserAccountMeta.find("user.id", userId).list();
        return metas.stream()
                .map(meta -> meta.account)
                .toList();
    }

    public List<UserPayment> getAccountPayments(final Long accountId) {
        final var outgoing = UserPayment.findBySourceAccountId(accountId);
        final var incoming = UserPayment.findByTargetAccountId(accountId);

        outgoing.addAll(incoming);
        return outgoing.stream()
                .sorted((p1, p2) -> p2.created.compareTo(p1.created))
                .toList();
    }

    public List<UserPayment> getOutgoingPayments(final Long accountId) {
        return UserPayment.findBySourceAccountId(accountId);
    }

    public List<UserPayment> getIncomingPayments(final Long accountId) {
        return UserPayment.findByTargetAccountId(accountId);
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
    public UserAccount createAccount(final UserAccount account) {
        account.persist();
        return account;
    }

    @Transactional
    public UserAccount updateAccount(final UserAccount account) {
        final UserAccount existingAccount = UserAccount.findById(account.id);
        if (existingAccount == null) {
            throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);
        }

        // Complete replacement (PUT semantics)
        existingAccount.name = account.name;

        existingAccount.persist();
        return existingAccount;
    }

    @Transactional
    public UserAccount patchAccount(final UserAccount account) {
        final UserAccount existingAccount = UserAccount.findById(account.id);
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
        final UserAccount account = UserAccount.findById(id);
        if (account == null) {
            return false;
        }

        // Check if account has any payments
        final var payments = this.getAccountPayments(id);
        if (!payments.isEmpty()) {
            throw new WebApplicationException("Cannot delete account with existing payments", Response.Status.CONFLICT);
        }

        return UserAccount.deleteById(id);
    }

    @Transactional
    public UserAccountMeta associateUserWithAccount(final Long userId, final Long accountId) {
        // Check if association already exists
        if (UserAccountMeta.existsByUserAndAccount(userId, accountId)) {
            throw new WebApplicationException("User is already associated with this account", Response.Status.CONFLICT);
        }

        final User user = User.findById(userId);
        final UserAccount account = UserAccount.findById(accountId);

        if (user == null) {
            throw new WebApplicationException("User not found", Response.Status.NOT_FOUND);
        }
        if (account == null) {
            throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);
        }

        final UserAccountMeta meta = new UserAccountMeta();
        meta.user = user;
        meta.account = account;
        meta.timestamp = LocalDateTime.now();
        meta.persist();

        return meta;
    }

    @Transactional
    public boolean removeUserFromAccount(final Long userId, final Long accountId) {
        final var meta = UserAccountMeta.findByUserAndAccount(userId, accountId);
        if (meta == null) {
            return false;
        }
        meta.delete();
        return true;
    }

    public boolean isUserAssociatedWithAccount(final Long userId, final Long accountId) {
        return UserAccountMeta.existsByUserAndAccount(userId, accountId);
    }
}
