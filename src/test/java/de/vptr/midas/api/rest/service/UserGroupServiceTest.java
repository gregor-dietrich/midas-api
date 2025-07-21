package de.vptr.midas.api.rest.service;

import static de.vptr.midas.api.util.ServiceTestUtil.assertServiceNotNull;
import static de.vptr.midas.api.util.ServiceTestUtil.createUniqueTestName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.vptr.midas.api.rest.dto.UserGroupDto;
import de.vptr.midas.api.rest.dto.UserGroupResponseDto;
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
        final List<UserGroupResponseDto> groups = this.userGroupService.getAllGroups();
        assertNotNull(groups);
    }

    @Test
    @Transactional
    void testCreateGroup() {
        final UserGroupDto newGroupDto = new UserGroupDto();
        newGroupDto.name = createUniqueTestName("Test Group");

        final UserGroupResponseDto createdGroup = this.userGroupService.createGroup(newGroupDto);

        assertNotNull(createdGroup);
        assertNotNull(createdGroup.id);
        assertEquals(newGroupDto.name, createdGroup.name);
    }

    @Test
    @Transactional
    void testUpdateGroup() {
        // First create a group
        final UserGroupDto newGroupDto = new UserGroupDto();
        newGroupDto.name = "Original Group";
        final UserGroupResponseDto createdGroup = this.userGroupService.createGroup(newGroupDto);

        // Update the group
        final UserGroupDto updateDto = new UserGroupDto();
        updateDto.name = "Updated Group";

        final UserGroupResponseDto updatedGroup = this.userGroupService.updateGroup(createdGroup.id, updateDto);

        assertNotNull(updatedGroup);
        assertEquals("Updated Group", updatedGroup.name);
    }

    @Test
    @Transactional
    void testDeleteGroup() {
        // First create a group
        final UserGroupDto newGroupDto = new UserGroupDto();
        newGroupDto.name = "Delete Test Group";
        final UserGroupResponseDto createdGroup = this.userGroupService.createGroup(newGroupDto);

        final Long groupId = createdGroup.id;

        final boolean deleted = this.userGroupService.deleteGroup(groupId);

        assertTrue(deleted);
        final Optional<UserGroupResponseDto> deletedGroup = this.userGroupService.findById(groupId);
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
        final UserGroupDto newGroupDto = new UserGroupDto();
        newGroupDto.name = "Find Test Group";
        final UserGroupResponseDto createdGroup = this.userGroupService.createGroup(newGroupDto);

        final Optional<UserGroupResponseDto> foundGroup = this.userGroupService.findById(createdGroup.id);

        assertTrue(foundGroup.isPresent());
        assertEquals(createdGroup.id, foundGroup.get().id);
        assertEquals("Find Test Group", foundGroup.get().name);
    }

    @Test
    void testFindByIdNonExistent() {
        final Optional<UserGroupResponseDto> foundGroup = this.userGroupService.findById(999999L);
        assertTrue(foundGroup.isEmpty());
    }
}
