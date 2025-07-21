package de.vptr.midas.api.rest.service;

import static de.vptr.midas.api.util.ServiceTestDataBuilder.createUniqueAccountEntity;
import static de.vptr.midas.api.util.ServiceTestUtil.assertServiceNotNull;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.vptr.midas.api.rest.entity.AccountEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
class AccountServiceTest {
    @Inject
    AccountService accountService;

    @Inject
    UserService userService;

    @Test
    void testServiceNotNull() {
        assertServiceNotNull(this.accountService);
    }

    @Test
    void testGetAllAccounts() {
        final List<AccountEntity> accounts = this.accountService.getAllAccounts();
        assertNotNull(accounts);
    }

    @Test
    @Transactional
    void testCreateAccount() {
        final AccountEntity newAccount = createUniqueAccountEntity();

        final AccountEntity createdAccount = this.accountService.createAccount(newAccount);

        assertNotNull(createdAccount);
        assertNotNull(createdAccount.id);
        assertEquals(newAccount.name, createdAccount.name);
    }

    @Test
    @Transactional
    void testUpdateAccount() {
        // First create an account
        final AccountEntity newAccount = createUniqueAccountEntity();
        final AccountEntity createdAccount = this.accountService.createAccount(newAccount);

        // Update the account
        createdAccount.name = "Updated Account";

        final AccountEntity updatedAccount = this.accountService.updateAccount(createdAccount);

        assertNotNull(updatedAccount);
        assertEquals("Updated Account", updatedAccount.name);
    }

    @Test
    @Transactional
    void testDeleteAccount() {
        // First create an account
        final AccountEntity newAccount = createUniqueAccountEntity();
        final AccountEntity createdAccount = this.accountService.createAccount(newAccount);

        final Long accountId = createdAccount.id;

        final boolean deleted = this.accountService.deleteAccount(accountId);

        assertTrue(deleted);
        final Optional<AccountEntity> deletedAccount = this.accountService.findById(accountId);
        assertTrue(deletedAccount.isEmpty());
    }

    @Test
    void testDeleteNonExistentAccount() {
        final boolean deleted = this.accountService.deleteAccount(999999L);
        assertFalse(deleted);
    }

    @Test
    @Transactional
    void testFindById() {
        // First create an account
        final AccountEntity newAccount = createUniqueAccountEntity();
        final AccountEntity createdAccount = this.accountService.createAccount(newAccount);

        final Optional<AccountEntity> foundAccount = this.accountService.findById(createdAccount.id);

        assertTrue(foundAccount.isPresent());
        assertEquals(createdAccount.id, foundAccount.get().id);
        assertEquals(newAccount.name, foundAccount.get().name);
    }

    @Test
    void testFindByIdNonExistent() {
        final Optional<AccountEntity> foundAccount = this.accountService.findById(999999L);
        assertTrue(foundAccount.isEmpty());
    }
}
