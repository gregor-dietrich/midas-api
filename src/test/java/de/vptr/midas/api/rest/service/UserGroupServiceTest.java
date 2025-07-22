package de.vptr.midas.api.rest.service;

import static de.vptr.midas.api.util.ServiceTestDataBuilder.createUniqueUserGroupDto;
import static de.vptr.midas.api.util.ServiceTestUtil.assertServiceNotNull;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

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
        assertServiceNotNull(this.userGroupService);
    }

    @Test
    void testGetAllGroups() {
        final var groups = this.userGroupService.getAllGroups();
        assertNotNull(groups);
    }

    @Test
    @Transactional
    void testCreateGroup() {
        final var newGroupDto = createUniqueUserGroupDto();

        final var createdGroup = this.userGroupService.createGroup(newGroupDto);

        assertNotNull(createdGroup);
        assertNotNull(createdGroup.id);
        assertEquals(newGroupDto.name, createdGroup.name);
    }

    @Test
    @Transactional
    void testUpdateGroup() {
        // First create a group
        final var newGroupDto = createUniqueUserGroupDto();
        final var createdGroup = this.userGroupService.createGroup(newGroupDto);

        // Update the group
        final var updateDto = createUniqueUserGroupDto();

        final var updatedGroup = this.userGroupService.updateGroup(createdGroup.id, updateDto);

        assertNotNull(updatedGroup);
        assertEquals(updateDto.name, updatedGroup.name);
    }

    @Test
    @Transactional
    void testDeleteGroup() {
        // First create a group
        final var newGroupDto = createUniqueUserGroupDto();
        final var createdGroup = this.userGroupService.createGroup(newGroupDto);

        final Long groupId = createdGroup.id;

        final var deleted = this.userGroupService.deleteGroup(groupId);

        assertTrue(deleted);
        final var deletedGroup = this.userGroupService.findById(groupId);
        assertTrue(deletedGroup.isEmpty());
    }

    @Test
    void testDeleteNonExistentGroup() {
        final var deleted = this.userGroupService.deleteGroup(999999L);
        assertFalse(deleted);
    }

    @Test
    @Transactional
    void testFindById() {
        // First create a group
        final var newGroupDto = createUniqueUserGroupDto();
        final var createdGroup = this.userGroupService.createGroup(newGroupDto);

        final var foundGroup = this.userGroupService.findById(createdGroup.id);

        assertTrue(foundGroup.isPresent());
        assertEquals(createdGroup.id, foundGroup.get().id);
        assertEquals(createdGroup.name, foundGroup.get().name);
    }

    @Test
    void testFindByIdNonExistent() {
        final var foundGroup = this.userGroupService.findById(999999L);
        assertTrue(foundGroup.isEmpty());
    }
}
