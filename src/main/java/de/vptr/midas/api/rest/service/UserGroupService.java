package de.vptr.midas.api.rest.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.dto.UserGroupDto;
import de.vptr.midas.api.rest.dto.UserGroupResponseDto;
import de.vptr.midas.api.rest.entity.UserEntity;
import de.vptr.midas.api.rest.entity.UserGroupEntity;
import de.vptr.midas.api.rest.entity.UserGroupMetaEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class UserGroupService {

    public List<UserGroupResponseDto> getAllGroups() {
        return UserGroupEntity.listAll().stream()
                .map(entity -> new UserGroupResponseDto((UserGroupEntity) entity))
                .toList();
    }

    public Optional<UserGroupResponseDto> findById(final Long id) {
        return UserGroupEntity.findByIdOptional(id)
                .map(entity -> new UserGroupResponseDto((UserGroupEntity) entity));
    }

    public Optional<UserGroupResponseDto> findByName(final String name) {
        return Optional.ofNullable(UserGroupEntity.findByName(name))
                .map(UserGroupResponseDto::new);
    }

    public List<UserEntity> getUsersInGroup(final Long groupId) {
        final UserGroupEntity group = UserGroupEntity.findById(groupId);
        if (group == null) {
            throw new WebApplicationException("Group not found", Response.Status.NOT_FOUND);
        }
        return group.getUsers();
    }

    public List<UserGroupResponseDto> getGroupsForUser(final Long userId) {
        final var metas = UserGroupMetaEntity.findByUserId(userId);
        return metas.stream()
                .map(meta -> new UserGroupResponseDto(meta.group))
                .toList();
    }

    @Transactional
    public UserGroupResponseDto createGroup(final UserGroupDto groupDto) {
        if (groupDto.name == null || groupDto.name.trim().isEmpty()) {
            throw new ValidationException("Name is required");
        }

        final UserGroupEntity group = new UserGroupEntity();
        group.name = groupDto.name;
        group.persist();

        return new UserGroupResponseDto(group);
    }

    @Transactional
    public UserGroupResponseDto updateGroup(final Long id, final UserGroupDto groupDto) {
        if (groupDto.name == null || groupDto.name.trim().isEmpty()) {
            throw new ValidationException("Name is required");
        }

        final UserGroupEntity existingGroup = UserGroupEntity.findById(id);
        if (existingGroup == null) {
            throw new WebApplicationException("Group not found", Response.Status.NOT_FOUND);
        }

        // Complete replacement (PUT semantics)
        existingGroup.name = groupDto.name;
        existingGroup.persist();

        return new UserGroupResponseDto(existingGroup);
    }

    @Transactional
    public UserGroupResponseDto patchGroup(final Long id, final UserGroupDto groupDto) {
        final UserGroupEntity existingGroup = UserGroupEntity.findById(id);
        if (existingGroup == null) {
            throw new WebApplicationException("Group not found", Response.Status.NOT_FOUND);
        }

        // Partial update (PATCH semantics) - only update provided fields
        if (groupDto.name != null && !groupDto.name.trim().isEmpty()) {
            existingGroup.name = groupDto.name;
        }

        existingGroup.persist();
        return new UserGroupResponseDto(existingGroup);
    }

    @Transactional
    public boolean deleteGroup(final Long id) {
        return UserGroupEntity.deleteById(id);
    }

    @Transactional
    public UserGroupMetaEntity addUserToGroup(final Long userId, final Long groupId) {
        // Check if association already exists
        if (UserGroupMetaEntity.isUserInGroup(userId, groupId)) {
            throw new WebApplicationException("User is already in this group", Response.Status.CONFLICT);
        }

        final UserEntity user = UserEntity.findById(userId);
        final UserGroupEntity group = UserGroupEntity.findById(groupId);

        if (user == null) {
            throw new WebApplicationException("User not found", Response.Status.NOT_FOUND);
        }
        if (group == null) {
            throw new WebApplicationException("Group not found", Response.Status.NOT_FOUND);
        }

        final var meta = new UserGroupMetaEntity();
        meta.user = user;
        meta.group = group;
        meta.timestamp = LocalDateTime.now();
        meta.persist();

        return meta;
    }

    @Transactional
    public boolean removeUserFromGroup(final Long userId, final Long groupId) {
        final var meta = UserGroupMetaEntity.findByUserAndGroup(userId, groupId);
        if (meta == null) {
            return false;
        }
        meta.delete();
        return true;
    }

    public boolean isUserInGroup(final Long userId, final Long groupId) {
        return UserGroupMetaEntity.isUserInGroup(userId, groupId);
    }
}
