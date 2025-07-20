package de.vptr.midas.api.rest.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.vptr.midas.api.rest.entity.UserGroupEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
class UserGroupServiceTest {
    @Inject
    UserGroupService userGroupService;

    @Inject
    UserService userService;

    @Test
    void testServiceNotNull() {
        assertNotNull(this.userGroupService);
    }

    @Test
    void testGetAllGroups() {
        final List<UserGroupEntity> groups = this.userGroupService.getAllGroups();
        assertNotNull(groups);
    }

    @Test
    @Transactional
    void testCreateGroup() {
        final UserGroupEntity newGroup = new UserGroupEntity();
        newGroup.name = "Test Group";

        final UserGroupEntity createdGroup = this.userGroupService.createGroup(newGroup);

        assertNotNull(createdGroup);
        assertNotNull(createdGroup.id);
        assertEquals("Test Group", createdGroup.name);
    }

    @Test
    @Transactional
    void testUpdateGroup() {
        // First create a group
        final UserGroupEntity newGroup = new UserGroupEntity();
        newGroup.name = "Original Group";
        final UserGroupEntity createdGroup = this.userGroupService.createGroup(newGroup);

        // Update the group
        createdGroup.name = "Updated Group";

        final UserGroupEntity updatedGroup = this.userGroupService.updateGroup(createdGroup);

        assertNotNull(updatedGroup);
        assertEquals("Updated Group", updatedGroup.name);
    }

    @Test
    @Transactional
    void testDeleteGroup() {
        // First create a group
        final UserGroupEntity newGroup = new UserGroupEntity();
        newGroup.name = "Delete Test Group";
        final UserGroupEntity createdGroup = this.userGroupService.createGroup(newGroup);

        final Long groupId = createdGroup.id;

        final boolean deleted = this.userGroupService.deleteGroup(groupId);

        assertTrue(deleted);
        final Optional<UserGroupEntity> deletedGroup = this.userGroupService.findById(groupId);
        assertTrue(deletedGroup.isEmpty());
    }

    @Test
    void testDeleteNonExistentGroup() {
        final boolean deleted = this.userGroupService.deleteGroup(999999L);
        assertFalse(deleted);
    }

    @Test
    @Transactional
    void testFindById() {
        // First create a group
        final UserGroupEntity newGroup = new UserGroupEntity();
        newGroup.name = "Find Test Group";
        final UserGroupEntity createdGroup = this.userGroupService.createGroup(newGroup);

        final Optional<UserGroupEntity> foundGroup = this.userGroupService.findById(createdGroup.id);

        assertTrue(foundGroup.isPresent());
        assertEquals(createdGroup.id, foundGroup.get().id);
        assertEquals("Find Test Group", foundGroup.get().name);
    }

    @Test
    void testFindByIdNonExistent() {
        final Optional<UserGroupEntity> foundGroup = this.userGroupService.findById(999999L);
        assertTrue(foundGroup.isEmpty());
    }
}
