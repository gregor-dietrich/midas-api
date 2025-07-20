package de.vptr.midas.api.rest.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.vptr.midas.api.rest.dto.UserDto;
import de.vptr.midas.api.rest.dto.UserRankDto;
import de.vptr.midas.api.rest.dto.UserRankResponseDto;
import de.vptr.midas.api.rest.entity.UserRankEntity;
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
        // Create test user with unique username
        final String uniqueSuffix = String.valueOf(System.currentTimeMillis() + (int) (Math.random() * 10000));
        final UserDto testUserDto = new UserDto();
        testUserDto.username = "rankTestUser_" + uniqueSuffix;
        testUserDto.email = "ranktest_" + uniqueSuffix + "@example.com";
        testUserDto.password = "password";
        this.userService.createUser(testUserDto);
    }

    @Test
    void testServiceNotNull() {
        assertNotNull(this.userRankService);
    }

    @Test
    void testGetAllRanks() {
        final List<UserRankEntity> ranks = this.userRankService.getAllRanks();
        assertNotNull(ranks);
    }

    @Test
    @Transactional
    void testCreateRank() {
        final UserRankDto newRank = new UserRankDto();
        final String uniqueName = "Test Rank " + System.currentTimeMillis() + Math.random();
        newRank.name = uniqueName;
        newRank.userAdd = true;
        newRank.userEdit = true;
        newRank.userDelete = false;
        newRank.postAdd = true;
        newRank.postEdit = false;
        newRank.postDelete = false;

        final UserRankResponseDto createdRank = this.userRankService.createRank(newRank);

        assertNotNull(createdRank);
        assertNotNull(createdRank.id);
        assertEquals(uniqueName, createdRank.name);
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
        final UserRankDto firstRank = new UserRankDto();
        firstRank.name = "Duplicate Rank";
        firstRank.userAdd = false;
        firstRank.userEdit = false;
        firstRank.userDelete = false;
        this.userRankService.createRank(firstRank);

        // Try to create another rank with the same name
        final UserRankDto secondRank = new UserRankDto();
        secondRank.name = "Duplicate Rank";
        secondRank.userAdd = true;
        secondRank.userEdit = true;
        secondRank.userDelete = true;

        assertThrows(Exception.class, () -> {
            this.userRankService.createRank(secondRank);
        });
    }

    @Test
    @Transactional
    void testUpdateRank() {
        // First create a rank
        final UserRankDto newRank = new UserRankDto();
        newRank.name = "Update Test Rank";
        newRank.userAdd = false;
        newRank.userEdit = false;
        newRank.userDelete = false;
        newRank.postAdd = false;
        newRank.postEdit = false;
        newRank.postDelete = false;
        final UserRankResponseDto createdRank = this.userRankService.createRank(newRank);

        // Update the rank
        final UserRankDto updateDto = new UserRankDto();
        updateDto.name = "Updated Rank Name";
        updateDto.userAdd = true;
        updateDto.userEdit = true;
        updateDto.postAdd = true;
        updateDto.userDelete = false;
        updateDto.postEdit = false;
        updateDto.postDelete = false;

        final UserRankResponseDto updatedRank = this.userRankService.updateRank(createdRank.id, updateDto);

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
        final UserRankDto newRank = new UserRankDto();
        newRank.name = "Delete Test Rank";
        newRank.userAdd = false;
        newRank.userEdit = false;
        newRank.userDelete = false;
        final UserRankResponseDto createdRank = this.userRankService.createRank(newRank);

        final Long rankId = createdRank.id;

        final boolean deleted = this.userRankService.deleteRank(rankId);

        assertTrue(deleted);
        final Optional<UserRankEntity> deletedRank = this.userRankService.findById(rankId);
        assertTrue(deletedRank.isEmpty());
    }

    @Test
    void testDeleteNonExistentRank() {
        final boolean deleted = this.userRankService.deleteRank(999999L);
        assertFalse(deleted);
    }

    @Test
    @Transactional
    void testFindById() {
        // First create a rank
        final UserRankDto newRank = new UserRankDto();
        newRank.name = "Find By ID Rank";
        newRank.userAdd = true;
        newRank.userEdit = false;
        newRank.userDelete = false;
        final UserRankResponseDto createdRank = this.userRankService.createRank(newRank);

        final Optional<UserRankEntity> foundRank = this.userRankService.findById(createdRank.id);

        assertTrue(foundRank.isPresent());
        assertEquals("Find By ID Rank", foundRank.get().name);
        assertTrue(foundRank.get().userAdd);
    }

    @Test
    void testFindByIdNonExistent() {
        final Optional<UserRankEntity> foundRank = this.userRankService.findById(999999L);
        assertTrue(foundRank.isEmpty());
    }

    @Test
    @Transactional
    void testFindByName() {
        // First create a rank
        final UserRankDto newRank = new UserRankDto();
        newRank.name = "Find By Name Rank";
        newRank.userAdd = false;
        newRank.userEdit = true;
        newRank.userDelete = false;
        this.userRankService.createRank(newRank);

        final Optional<UserRankEntity> foundRank = this.userRankService.findByName("Find By Name Rank");

        assertTrue(foundRank.isPresent());
        assertEquals("Find By Name Rank", foundRank.get().name);
        assertFalse(foundRank.get().userAdd);
        assertTrue(foundRank.get().userEdit);
    }

    @Test
    void testFindByNameNonExistent() {
        final Optional<UserRankEntity> foundRank = this.userRankService.findByName("Non Existent Rank");
        assertTrue(foundRank.isEmpty());
    }

    @Test
    @Transactional
    void testGetRanksWithUserPermissions() {
        // Create ranks with different user permissions
        final UserRankDto userRank = new UserRankDto();
        userRank.name = "User Permission Rank";
        userRank.userAdd = true;
        userRank.userEdit = true;
        userRank.userDelete = true;
        userRank.postAdd = false;
        userRank.postEdit = false;
        userRank.postDelete = false;
        this.userRankService.createRank(userRank);

        final UserRankDto noUserRank = new UserRankDto();
        noUserRank.name = "No User Permission Rank";
        noUserRank.userAdd = false;
        noUserRank.userEdit = false;
        noUserRank.userDelete = false;
        noUserRank.postAdd = true;
        noUserRank.postEdit = true;
        noUserRank.postDelete = true;
        this.userRankService.createRank(noUserRank);

        final List<UserRankEntity> userPermissionRanks = this.userRankService.getAllRanks().stream()
                .filter(rank -> rank.userAdd || rank.userEdit || rank.userDelete)
                .toList();

        assertNotNull(userPermissionRanks);
        // Verify all returned ranks have at least one user permission
        for (final UserRankEntity rank : userPermissionRanks) {
            assertTrue(rank.userAdd || rank.userEdit || rank.userDelete);
        }
    }

    @Test
    @Transactional
    void testGetRanksWithPostPermissions() {
        // Create ranks with different post permissions
        final UserRankDto postRank = new UserRankDto();
        postRank.name = "Post Permission Rank";
        postRank.userAdd = false;
        postRank.userEdit = false;
        postRank.userDelete = false;
        postRank.postAdd = true;
        postRank.postEdit = true;
        postRank.postDelete = true;
        this.userRankService.createRank(postRank);

        final UserRankDto noPostRank = new UserRankDto();
        noPostRank.name = "No Post Permission Rank";
        noPostRank.userAdd = true;
        noPostRank.userEdit = true;
        noPostRank.userDelete = true;
        noPostRank.postAdd = false;
        noPostRank.postEdit = false;
        noPostRank.postDelete = false;
        this.userRankService.createRank(noPostRank);

        final List<UserRankEntity> postPermissionRanks = this.userRankService.getAllRanks().stream()
                .filter(rank -> rank.postAdd || rank.postEdit || rank.postDelete)
                .toList();

        assertNotNull(postPermissionRanks);
        // Verify all returned ranks have at least one post permission
        for (final UserRankEntity rank : postPermissionRanks) {
            assertTrue(rank.postAdd || rank.postEdit || rank.postDelete);
        }
    }
}
