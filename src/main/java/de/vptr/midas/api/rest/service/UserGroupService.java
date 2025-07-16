package de.vptr.midas.api.rest.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.entity.UserEntity;
import de.vptr.midas.api.rest.entity.UserGroupEntity;
import de.vptr.midas.api.rest.entity.UserGroupMetaEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class UserGroupService {

    public List<UserGroupEntity> getAllGroups() {
        return UserGroupEntity.listAll();
    }

    public Optional<UserGroupEntity> findById(final Long id) {
        return UserGroupEntity.findByIdOptional(id);
    }

    public Optional<UserGroupEntity> findByName(final String name) {
        return Optional.ofNullable(UserGroupEntity.findByName(name));
    }

    public List<UserEntity> getUsersInGroup(final Long groupId) {
        final UserGroupEntity group = UserGroupEntity.findById(groupId);
        if (group == null) {
            throw new WebApplicationException("Group not found", Response.Status.NOT_FOUND);
        }
        return group.getUsers();
    }

    public List<UserGroupEntity> getGroupsForUser(final Long userId) {
        final var metas = UserGroupMetaEntity.findByUserId(userId);
        return metas.stream()
                .map(meta -> meta.group)
                .toList();
    }

    @Transactional
    public UserGroupEntity createGroup(final UserGroupEntity group) {
        group.persist();
        return group;
    }

    @Transactional
    public UserGroupEntity updateGroup(final UserGroupEntity group) {
        final UserGroupEntity existingGroup = UserGroupEntity.findById(group.id);
        if (existingGroup == null) {
            throw new WebApplicationException("Group not found", Response.Status.NOT_FOUND);
        }

        // Complete replacement (PUT semantics)
        existingGroup.name = group.name;

        existingGroup.persist();
        return existingGroup;
    }

    @Transactional
    public UserGroupEntity patchGroup(final UserGroupEntity group) {
        final UserGroupEntity existingGroup = UserGroupEntity.findById(group.id);
        if (existingGroup == null) {
            throw new WebApplicationException("Group not found", Response.Status.NOT_FOUND);
        }

        // Partial update (PATCH semantics) - only update provided fields
        if (group.name != null) {
            existingGroup.name = group.name;
        }

        existingGroup.persist();
        return existingGroup;
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
