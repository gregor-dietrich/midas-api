package de.vptr.midas.api.rest.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.vptr.midas.api.rest.entity.UserAccountEntity;
import de.vptr.midas.api.rest.entity.UserEntity;
import de.vptr.midas.api.rest.entity.UserRankEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
class UserAccountServiceTest {
    @Inject
    UserAccountService userAccountService;

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
        final String uniqueSuffix = String.valueOf(System.currentTimeMillis() + (int)(Math.random()*10000));
        this.testUser = new UserEntity();
        this.testUser.username = "accountTestUser_" + uniqueSuffix;
        this.testUser.email = "accounttest_" + uniqueSuffix + "@example.com";
        this.testUser.password = "password";
        this.testUser = this.userService.createUser(this.testUser);
    }

    @Test
    void testServiceNotNull() {
        assertNotNull(this.userAccountService);
    }

    @Test
    void testGetAllAccounts() {
        final List<UserAccountEntity> accounts = this.userAccountService.getAllAccounts();
        assertNotNull(accounts);
    }

    @Test
    @Transactional
    void testCreateAccount() {
        final UserAccountEntity newAccount = new UserAccountEntity();
        newAccount.name = "Test Account";

        final UserAccountEntity createdAccount = this.userAccountService.createAccount(newAccount);

        assertNotNull(createdAccount);
        assertNotNull(createdAccount.id);
        assertEquals("Test Account", createdAccount.name);
    }

    @Test
    @Transactional
    void testUpdateAccount() {
        // First create an account
        final UserAccountEntity newAccount = new UserAccountEntity();
        newAccount.name = "Original Account";
        final UserAccountEntity createdAccount = this.userAccountService.createAccount(newAccount);

        // Update the account
        createdAccount.name = "Updated Account";

        final UserAccountEntity updatedAccount = this.userAccountService.updateAccount(createdAccount);

        assertNotNull(updatedAccount);
        assertEquals("Updated Account", updatedAccount.name);
    }

    @Test
    @Transactional
    void testDeleteAccount() {
        // First create an account
        final UserAccountEntity newAccount = new UserAccountEntity();
        newAccount.name = "Delete Test Account";
        final UserAccountEntity createdAccount = this.userAccountService.createAccount(newAccount);

        final Long accountId = createdAccount.id;

        final boolean deleted = this.userAccountService.deleteAccount(accountId);

        assertTrue(deleted);
        final Optional<UserAccountEntity> deletedAccount = this.userAccountService.findById(accountId);
        assertTrue(deletedAccount.isEmpty());
    }

    @Test
    void testDeleteNonExistentAccount() {
        final boolean deleted = this.userAccountService.deleteAccount(999999L);
        assertFalse(deleted);
    }

    @Test
    @Transactional
    void testFindById() {
        // First create an account
        final UserAccountEntity newAccount = new UserAccountEntity();
        newAccount.name = "Find Test Account";
        final UserAccountEntity createdAccount = this.userAccountService.createAccount(newAccount);

        final Optional<UserAccountEntity> foundAccount = this.userAccountService.findById(createdAccount.id);

        assertTrue(foundAccount.isPresent());
        assertEquals(createdAccount.id, foundAccount.get().id);
        assertEquals("Find Test Account", foundAccount.get().name);
    }

    @Test
    void testFindByIdNonExistent() {
        final Optional<UserAccountEntity> foundAccount = this.userAccountService.findById(999999L);
        assertTrue(foundAccount.isEmpty());
    }
}
