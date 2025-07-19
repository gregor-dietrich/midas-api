package de.vptr.midas.api.rest.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.vptr.midas.api.rest.entity.AccountEntity;
import de.vptr.midas.api.rest.entity.UserEntity;
import de.vptr.midas.api.rest.entity.UserRankEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
class AccountServiceTest {
    @Inject
    AccountService accountService;

    @Inject
    UserService userService;

    private UserEntity testUser;
    private UserRankEntity testRank;

    @BeforeEach
    @Transactional
    void setUp() {
        // Create test rank if it doesn't exist
        this.testRank = UserRankEntity.find("name", "Test Rank").firstResult();
        if (this.testRank == null) {
            this.testRank = new UserRankEntity();
            this.testRank.name = "Test Rank";
            this.testRank.userAdd = false;
            this.testRank.userEdit = false;
            this.testRank.userDelete = false;
            this.testRank.persist();
        }

        // Create test user with unique username
        final String uniqueSuffix = String.valueOf(System.currentTimeMillis() + (int) (Math.random() * 10000));
        this.testUser = new UserEntity();
        this.testUser.username = "accountTestUser_" + uniqueSuffix;
        this.testUser.email = "accounttest_" + uniqueSuffix + "@example.com";
        this.testUser.password = "password";
        this.testUser = this.userService.createUser(this.testUser);
    }

    @Test
    void testServiceNotNull() {
        assertNotNull(this.accountService);
    }

    @Test
    void testGetAllAccounts() {
        final List<AccountEntity> accounts = this.accountService.getAllAccounts();
        assertNotNull(accounts);
    }

    @Test
    @Transactional
    void testCreateAccount() {
        final AccountEntity newAccount = new AccountEntity();
        newAccount.name = "Test Account";

        final AccountEntity createdAccount = this.accountService.createAccount(newAccount);

        assertNotNull(createdAccount);
        assertNotNull(createdAccount.id);
        assertEquals("Test Account", createdAccount.name);
    }

    @Test
    @Transactional
    void testUpdateAccount() {
        // First create an account
        final AccountEntity newAccount = new AccountEntity();
        newAccount.name = "Original Account";
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
        final AccountEntity newAccount = new AccountEntity();
        newAccount.name = "Delete Test Account";
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
        final AccountEntity newAccount = new AccountEntity();
        newAccount.name = "Find Test Account";
        final AccountEntity createdAccount = this.accountService.createAccount(newAccount);

        final Optional<AccountEntity> foundAccount = this.accountService.findById(createdAccount.id);

        assertTrue(foundAccount.isPresent());
        assertEquals(createdAccount.id, foundAccount.get().id);
        assertEquals("Find Test Account", foundAccount.get().name);
    }

    @Test
    void testFindByIdNonExistent() {
        final Optional<AccountEntity> foundAccount = this.accountService.findById(999999L);
        assertTrue(foundAccount.isEmpty());
    }
}
