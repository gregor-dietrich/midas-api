package de.vptr.midas.api.rest.service;

import static de.vptr.midas.api.util.ServiceTestDataBuilder.createUniqueUserDto;
import static de.vptr.midas.api.util.ServiceTestDataBuilder.createUserUpdateDto;
import static de.vptr.midas.api.util.ServiceTestUtil.assertServiceNotNull;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.vptr.midas.api.rest.dto.UserDto;
import de.vptr.midas.api.rest.dto.UserResponseDto;
import de.vptr.midas.api.rest.entity.UserEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
class UserServiceTest {
    @Inject
    UserService userService;

    @Inject
    UserRankService userRankService;

    @Test
    void testServiceNotNull() {
        assertServiceNotNull(this.userService);
    }

    @Test
    void testGetAllUsers() {
        final List<UserEntity> users = this.userService.getAllUsers();
        assertNotNull(users);
    }

    @Test
    @Transactional
    void testCreateUser() {
        final UserDto newUser = createUniqueUserDto("newTestUser", "newtest");
        final UserResponseDto createdUser = this.userService.createUser(newUser);

        assertNotNull(createdUser);
        assertNotNull(createdUser.id);
        assertEquals(newUser.username, createdUser.username);
        assertEquals(newUser.email, createdUser.email);
        // Password is not returned in response DTO
        assertNotNull(createdUser.created);
        assertNotNull(createdUser.lastLogin);
        assertFalse(createdUser.activated); // Default should be false
        assertFalse(createdUser.banned); // Default should be false
    }

    @Test
    @Transactional
    void testCreateUserWithExistingUsername() {
        // First create a user
        final UserDto firstUser = createUniqueUserDto("duplicateUser", "first");
        this.userService.createUser(firstUser);

        // Try to create another user with the same username
        final UserDto secondUser = createUniqueUserDto();
        secondUser.username = firstUser.username; // Use same username
        secondUser.email = createUniqueUserDto("second", "second").email; // Different email

        assertThrows(Exception.class, () -> {
            this.userService.createUser(secondUser);
        });
    }

    @Test
    @Transactional
    void testUpdateUser() {
        // First create a user
        final UserDto newUser = createUniqueUserDto("updateTestUser", "update");
        final UserResponseDto createdUser = this.userService.createUser(newUser);

        // Update the user
        final UserDto updateDto = createUserUpdateDto("testUser", "updated");

        final UserResponseDto updatedUser = this.userService.updateUser(createdUser.id, updateDto);

        assertNotNull(updatedUser);
        assertEquals(updateDto.email, updatedUser.email);
        assertTrue(updatedUser.activated);
    }

    @Test
    @Transactional
    void testDeleteUser() {
        // First create a user
        final UserDto newUser = createUniqueUserDto("deleteTestUser", "delete");
        final UserResponseDto createdUser = this.userService.createUser(newUser);

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
        // First create a user
        final UserDto newUser = createUniqueUserDto("findByUsernameUser", "findbyusername");
        this.userService.createUser(newUser);

        final var foundUser = this.userService.findByUsername(newUser.username);

        assertTrue(foundUser.isPresent());
        assertEquals(newUser.username, foundUser.get().username);
    }

    @Test
    void testFindByUsernameNonExistent() {
        final var foundUser = this.userService.findByUsername("nonExistentUser");
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @Transactional
    void testFindByEmail() {
        // First create a user
        final UserDto newUser = createUniqueUserDto("findByEmailUser", "findbyemail");
        this.userService.createUser(newUser);

        final var foundUser = this.userService.findByEmail(newUser.email);

        assertTrue(foundUser.isPresent());
        assertEquals(newUser.email, foundUser.get().email);
    }

    @Test
    void testFindByEmailNonExistent() {
        final var foundUser = this.userService.findByEmail("nonexistent@example.com");
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @Transactional
    void testFindActiveUsers() {
        // Create active and inactive users
        final UserDto activeUser = createUniqueUserDto("activeUser", "active");
        activeUser.activated = true;
        activeUser.banned = false;
        this.userService.createUser(activeUser);

        final UserDto bannedUser = createUniqueUserDto("bannedUser", "banned");
        bannedUser.activated = true;
        bannedUser.banned = true;
        this.userService.createUser(bannedUser);

        // Flush using Panache to ensure users are persisted and visible to queries
        de.vptr.midas.api.rest.entity.UserEntity.getEntityManager().flush();

        final List<UserEntity> activeUsers = this.userService.findActiveUsers();
        assertNotNull(activeUsers);
        assertTrue(activeUsers.stream().anyMatch(u -> activeUser.username.equals(u.username)));
        assertFalse(activeUsers.stream().anyMatch(u -> bannedUser.username.equals(u.username)));
    }
}
