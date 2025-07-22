package de.vptr.midas.api.rest.service;

import static de.vptr.midas.api.util.ServiceTestUtil.assertServiceNotNull;
import static de.vptr.midas.api.util.ServiceTestUtil.setupTestUser;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.vptr.midas.api.rest.entity.UserRankEntity;
import de.vptr.midas.api.util.ServiceTestUtil;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
class UserRankServiceTest {
    @Inject
    UserRankService userRankService;

    @Inject
    UserService userService;

    @BeforeEach
    @Transactional
    void setUp() {
        // Create test user using utility
        setupTestUser(this.userService);
    }

    @Test
    void testServiceNotNull() {
        assertServiceNotNull(this.userRankService);
    }

    @Test
    void testGetAllRanks() {
        final var ranks = this.userRankService.getAllRanks();
        assertNotNull(ranks);
    }

    @Test
    @Transactional
    void testCreateRank() {
        final var newRank = de.vptr.midas.api.util.ServiceTestDataBuilder.createUserRankDto(
                ServiceTestUtil.createUniqueTestName("Test Rank"), // always non-blank
                true, // userAdd
                true, // userEdit
                false, // userDelete
                true, // postAdd
                false, // postEdit
                false // postDelete
        );
        // Override some permissions for testing
        newRank.userDelete = false;
        newRank.postEdit = false;
        newRank.postDelete = false;

        final var createdRank = this.userRankService.createRank(newRank);

        assertNotNull(createdRank);
        assertNotNull(createdRank.id);
        assertEquals(newRank.name, createdRank.name);
        assertTrue(createdRank.userAdd);
        assertTrue(createdRank.userEdit);
        assertFalse(createdRank.userDelete);
        assertTrue(createdRank.postAdd);
        assertFalse(createdRank.postEdit);
        assertFalse(createdRank.postDelete);
    }

    @Test
    @Transactional
    void testCreateRankWithExistingName() {
        // First create a rank
        final var firstRank = de.vptr.midas.api.util.ServiceTestDataBuilder.createUserRankDto(
                ServiceTestUtil.createUniqueTestName("Test Rank"), false, false, false, true, true, true);
        firstRank.userAdd = false;
        firstRank.userEdit = false;
        firstRank.userDelete = false;
        final var createdRank = this.userRankService.createRank(firstRank);

        // Try to create another rank with the same name
        final var secondRank = de.vptr.midas.api.util.ServiceTestDataBuilder.createUserRankDto(
                createdRank.name, true, true, true, false, false, false);

        assertThrows(Exception.class, () -> {
            this.userRankService.createRank(secondRank);
        });
    }

    @Test
    @TestTransaction
    void testUpdateRank() {
        // First create a rank
        final var newRank = de.vptr.midas.api.util.ServiceTestDataBuilder.createUserRankDto(
                "Update Test Rank", false, false, false, false, false, false);
        final var createdRank = this.userRankService.createRank(newRank);

        // Update the rank
        final var updateDto = de.vptr.midas.api.util.ServiceTestDataBuilder.createUserRankUpdateDto(
                "Updated Rank Name",
                true, // userAdd
                true, // userEdit
                false, // userDelete
                true, // postAdd
                false, // postEdit
                false // postDelete
        );

        final var updatedRank = this.userRankService.updateRank(createdRank.id, updateDto);

        assertNotNull(updatedRank);
        assertEquals("Updated Rank Name", updatedRank.name);
        assertTrue(updatedRank.userAdd);
        assertTrue(updatedRank.userEdit);
        assertTrue(updatedRank.postAdd);
        assertFalse(updatedRank.userDelete);
        assertFalse(updatedRank.postEdit);
        assertFalse(updatedRank.postDelete);
    }

    @Test
    @Transactional
    void testDeleteRank() {
        // First create a rank
        final var newRank = de.vptr.midas.api.util.ServiceTestDataBuilder.createUserRankDto(
                "Delete Test Rank", false, false, false, false, false, false);
        final var createdRank = this.userRankService.createRank(newRank);

        final Long rankId = createdRank.id;

        final var deleted = this.userRankService.deleteRank(rankId);

        assertTrue(deleted);
        final var deletedRank = this.userRankService.findById(rankId);
        assertTrue(deletedRank.isEmpty());
    }

    @Test
    void testDeleteNonExistentRank() {
        final var deleted = this.userRankService.deleteRank(999999L);
        assertFalse(deleted);
    }

    @Test
    @TestTransaction
    void testFindById() {
        // First create a rank
        final var newRank = de.vptr.midas.api.util.ServiceTestDataBuilder.createUserRankDto(
                "Find By ID Rank", true, false, false, false, false, false);
        final var createdRank = this.userRankService.createRank(newRank);

        final var foundRank = this.userRankService.findById(createdRank.id);

        assertTrue(foundRank.isPresent());
        assertEquals("Find By ID Rank", foundRank.get().name);
        assertTrue(foundRank.get().userAdd);
    }

    @Test
    void testFindByIdNonExistent() {
        final var foundRank = this.userRankService.findById(999999L);
        assertTrue(foundRank.isEmpty());
    }

    @Test
    @TestTransaction
    void testFindByName() {
        // First create a rank
        final var newRank = de.vptr.midas.api.util.ServiceTestDataBuilder.createUserRankDto(
                "Find By Name Rank", false, true, false, false, false, false);
        this.userRankService.createRank(newRank);

        final var foundRank = this.userRankService.findByName("Find By Name Rank");

        assertTrue(foundRank.isPresent());
        assertEquals("Find By Name Rank", foundRank.get().name);
        assertFalse(foundRank.get().userAdd);
        assertTrue(foundRank.get().userEdit);
    }

    @Test
    void testFindByNameNonExistent() {
        final var foundRank = this.userRankService.findByName("Non Existent Rank");
        assertTrue(foundRank.isEmpty());
    }

    @Test
    @TestTransaction
    void testGetRanksWithUserPermissions() {
        // Create ranks with different user permissions
        final var userRank = de.vptr.midas.api.util.ServiceTestDataBuilder.createUserRankDto(
                "User Permission Rank", true, true, true, false, false, false);
        this.userRankService.createRank(userRank);

        final var noUserRank = de.vptr.midas.api.util.ServiceTestDataBuilder.createUserRankDto(
                "No User Permission Rank", false, false, false, true, true, true);
        this.userRankService.createRank(noUserRank);

        final var userPermissionRanks = this.userRankService.getAllRanks().stream()
                .filter(rank -> rank.userAdd || rank.userEdit || rank.userDelete)
                .toList();

        assertNotNull(userPermissionRanks);
        // Verify all returned ranks have at least one user permission
        for (final UserRankEntity rank : userPermissionRanks) {
            assertTrue(rank.userAdd || rank.userEdit || rank.userDelete);
        }
    }

    @Test
    @TestTransaction
    void testGetRanksWithPostPermissions() {
        // Create ranks with different post permissions
        final var postRank = de.vptr.midas.api.util.ServiceTestDataBuilder.createUserRankDto(
                "Post Permission Rank", false, false, false, true, true, true);
        this.userRankService.createRank(postRank);

        final var noPostRank = de.vptr.midas.api.util.ServiceTestDataBuilder.createUserRankDto(
                "No Post Permission Rank", true, true, true, false, false, false);
        this.userRankService.createRank(noPostRank);

        final var postPermissionRanks = this.userRankService.getAllRanks().stream()
                .filter(rank -> rank.postAdd || rank.postEdit || rank.postDelete)
                .toList();

        assertNotNull(postPermissionRanks);
        // Verify all returned ranks have at least one post permission
        for (final UserRankEntity rank : postPermissionRanks) {
            assertTrue(rank.postAdd || rank.postEdit || rank.postDelete);
        }
    }
}
