package de.vptr.midas.api.rest.service;

import static de.vptr.midas.api.util.ServiceTestDataBuilder.createUniqueAccountEntity;
import static de.vptr.midas.api.util.ServiceTestUtil.assertServiceNotNull;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

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
        final var accounts = this.accountService.getAllAccounts();
        assertNotNull(accounts);
    }

    @Test
    @Transactional
    void testCreateAccount() {
        final var newAccount = createUniqueAccountEntity();

        final var createdAccount = this.accountService.createAccount(newAccount);

        assertNotNull(createdAccount);
        assertNotNull(createdAccount.id);
        assertEquals(newAccount.name, createdAccount.name);
    }

    @Test
    @Transactional
    void testUpdateAccount() {
        // First create an account
        final var newAccount = createUniqueAccountEntity();
        final var createdAccount = this.accountService.createAccount(newAccount);

        // Update the account
        createdAccount.name = "Updated Account";

        final var updatedAccount = this.accountService.updateAccount(createdAccount);

        assertNotNull(updatedAccount);
        assertEquals("Updated Account", updatedAccount.name);
    }

    @Test
    @Transactional
    void testDeleteAccount() {
        // First create an account
        final var newAccount = createUniqueAccountEntity();
        final var createdAccount = this.accountService.createAccount(newAccount);

        final Long accountId = createdAccount.id;

        final var deleted = this.accountService.deleteAccount(accountId);

        assertTrue(deleted);
        final var deletedAccount = this.accountService.findById(accountId);
        assertTrue(deletedAccount.isEmpty());
    }

    @Test
    void testDeleteNonExistentAccount() {
        final var deleted = this.accountService.deleteAccount(999999L);
        assertFalse(deleted);
    }

    @Test
    @Transactional
    void testFindById() {
        // First create an account
        final var newAccount = createUniqueAccountEntity();
        final var createdAccount = this.accountService.createAccount(newAccount);

        final var foundAccount = this.accountService.findById(createdAccount.id);

        assertTrue(foundAccount.isPresent());
        assertEquals(createdAccount.id, foundAccount.get().id);
        assertEquals(newAccount.name, foundAccount.get().name);
    }

    @Test
    void testFindByIdNonExistent() {
        final var foundAccount = this.accountService.findById(999999L);
        assertTrue(foundAccount.isEmpty());
    }
}
