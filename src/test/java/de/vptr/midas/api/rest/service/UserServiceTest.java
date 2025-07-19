package de.vptr.midas.api.rest.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.vptr.midas.api.rest.entity.UserEntity;
import de.vptr.midas.api.rest.entity.UserRankEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
class UserServiceTest {
    @Inject
    UserService userService;

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
        final UserEntity newUser = new UserEntity();
        newUser.username = "testUser" + uniqueSuffix;
        newUser.email = "testuser" + uniqueSuffix + "@example.com";
        newUser.password = "plainPassword";

        this.userService.createUser(newUser);
    }

    @Test
    void testServiceNotNull() {
        assertNotNull(this.userService);
    }

    @Test
    void testGetAllUsers() {
        final List<UserEntity> users = this.userService.getAllUsers();
        assertNotNull(users);
    }

    @Test
    @Transactional
    void testCreateUser() {
        final String uniqueSuffix = String.valueOf(System.currentTimeMillis() + (int) (Math.random() * 10000));
        final UserEntity newUser = new UserEntity();
        newUser.username = "newTestUser_" + uniqueSuffix;
        newUser.email = "newtest_" + uniqueSuffix + "@example.com";
        newUser.password = "plainPassword";
        final UserEntity createdUser = this.userService.createUser(newUser);

        assertNotNull(createdUser);
        assertNotNull(createdUser.id);
        assertEquals("newTestUser_" + uniqueSuffix, createdUser.username);
        assertEquals("newtest_" + uniqueSuffix + "@example.com", createdUser.email);
        assertNotEquals("plainPassword", createdUser.password); // Should be hashed
        assertNotNull(createdUser.salt);
        assertNotNull(createdUser.activationKey);
        assertNotNull(createdUser.created);
        assertNotNull(createdUser.lastLogin);
        assertFalse(createdUser.activated); // Default should be false
        assertFalse(createdUser.banned); // Default should be false
    }

    @Test
    @Transactional
    void testCreateUserWithExistingUsername() {
        final String uniqueSuffix = String.valueOf(System.currentTimeMillis() + (int) (Math.random() * 10000));
        // First create a user
        final UserEntity firstUser = new UserEntity();
        firstUser.username = "duplicateUser_" + uniqueSuffix;
        firstUser.email = "first_" + uniqueSuffix + "@example.com";
        firstUser.password = "password1";
        this.userService.createUser(firstUser);

        // Try to create another user with the same username
        final UserEntity secondUser = new UserEntity();
        secondUser.username = "duplicateUser_" + uniqueSuffix;
        secondUser.email = "second_" + uniqueSuffix + "@example.com";
        secondUser.password = "password2";

        assertThrows(Exception.class, () -> {
            this.userService.createUser(secondUser);
        });
    }

    @Test
    @Transactional
    void testUpdateUser() {
        final String uniqueSuffix = String.valueOf(System.currentTimeMillis() + (int) (Math.random() * 10000));
        // First create a user
        final UserEntity newUser = new UserEntity();
        newUser.username = "updateTestUser_" + uniqueSuffix;
        newUser.email = "update_" + uniqueSuffix + "@example.com";
        newUser.password = "password";
        final UserEntity createdUser = this.userService.createUser(newUser);

        // Update the user
        createdUser.email = "updated_" + uniqueSuffix + "@example.com";
        createdUser.activated = true;

        final UserEntity updatedUser = this.userService.updateUser(createdUser);

        assertNotNull(updatedUser);
        assertEquals("updated_" + uniqueSuffix + "@example.com", updatedUser.email);
        assertTrue(updatedUser.activated);
    }

    @Test
    @Transactional
    void testDeleteUser() {
        final String uniqueSuffix = String.valueOf(System.currentTimeMillis() + (int) (Math.random() * 10000));
        // First create a user
        final UserEntity newUser = new UserEntity();
        newUser.username = "deleteTestUser_" + uniqueSuffix;
        newUser.email = "delete_" + uniqueSuffix + "@example.com";
        newUser.password = "password";
        final UserEntity createdUser = this.userService.createUser(newUser);

        final Long userId = createdUser.id;

        final boolean deleted = this.userService.deleteUser(userId);

        assertTrue(deleted);
        // UserEntity.findById returns null if not found
        final UserEntity deletedUser = UserEntity.findById(userId);
        assertNull(deletedUser);
    }

    @Test
    void testDeleteNonExistentUser() {
        final boolean deleted = this.userService.deleteUser(999999L);
        assertFalse(deleted);
    }

    @Test
    @Transactional
    void testFindByUsername() {
        final String uniqueSuffix = String.valueOf(System.currentTimeMillis() + (int) (Math.random() * 10000));
        // First create a user
        final UserEntity newUser = new UserEntity();
        newUser.username = "findByUsernameUser_" + uniqueSuffix;
        newUser.email = "findbyusername_" + uniqueSuffix + "@example.com";
        newUser.password = "password";
        this.userService.createUser(newUser);

        final var foundUser = this.userService.findByUsername("findByUsernameUser_" + uniqueSuffix);

        assertTrue(foundUser.isPresent());
        assertEquals("findByUsernameUser_" + uniqueSuffix, foundUser.get().username);
    }

    @Test
    void testFindByUsernameNonExistent() {
        final var foundUser = this.userService.findByUsername("nonExistentUser");
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @Transactional
    void testFindByEmail() {
        final String uniqueSuffix = String.valueOf(System.currentTimeMillis() + (int) (Math.random() * 10000));
        // First create a user
        final UserEntity newUser = new UserEntity();
        newUser.username = "findByEmailUser_" + uniqueSuffix;
        newUser.email = "findbyemail_" + uniqueSuffix + "@example.com";
        newUser.password = "password";
        this.userService.createUser(newUser);

        final var foundUser = this.userService.findByEmail("findbyemail_" + uniqueSuffix + "@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("findbyemail_" + uniqueSuffix + "@example.com", foundUser.get().email);
    }

    @Test
    void testFindByEmailNonExistent() {
        final var foundUser = this.userService.findByEmail("nonexistent@example.com");
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @Transactional
    void testFindActiveUsers() {
        final String uniqueSuffix = String.valueOf(System.currentTimeMillis() + (int) (Math.random() * 10000));
        // Create active and inactive users
        final UserEntity activeUser = new UserEntity();
        activeUser.username = "activeUser_" + uniqueSuffix;
        activeUser.email = "active_" + uniqueSuffix + "@example.com";
        activeUser.password = "password";
        activeUser.activated = true;
        activeUser.banned = false;
        this.userService.createUser(activeUser);

        final UserEntity bannedUser = new UserEntity();
        bannedUser.username = "bannedUser_" + uniqueSuffix;
        bannedUser.email = "banned_" + uniqueSuffix + "@example.com";
        bannedUser.password = "password";
        bannedUser.activated = true;
        bannedUser.banned = true;
        this.userService.createUser(bannedUser);

        // Flush using Panache to ensure users are persisted and visible to queries
        de.vptr.midas.api.rest.entity.UserEntity.getEntityManager().flush();

        final List<UserEntity> activeUsers = this.userService.findActiveUsers();
        assertNotNull(activeUsers);
        assertTrue(activeUsers.stream().anyMatch(u -> ("activeUser_" + uniqueSuffix).equals(u.username)));
        assertFalse(activeUsers.stream().anyMatch(u -> ("bannedUser_" + uniqueSuffix).equals(u.username)));
    }
}
