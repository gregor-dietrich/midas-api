package de.vptr.midas.api.rest.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.entity.User;
import de.vptr.midas.api.rest.entity.UserGroup;
import de.vptr.midas.api.rest.entity.UserGroupMeta;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class UserGroupService {

    public List<UserGroup> getAllGroups() {
        return UserGroup.listAll();
    }

    public Optional<UserGroup> findById(final Long id) {
        return UserGroup.findByIdOptional(id);
    }

    public Optional<UserGroup> findByName(final String name) {
        return Optional.ofNullable(UserGroup.findByName(name));
    }

    public List<User> getUsersInGroup(final Long groupId) {
        final UserGroup group = UserGroup.findById(groupId);
        if (group == null) {
            throw new WebApplicationException("Group not found", Response.Status.NOT_FOUND);
        }
        return group.getUsers();
    }

    public List<UserGroup> getGroupsForUser(final Long userId) {
        final var metas = UserGroupMeta.findByUserId(userId);
        return metas.stream()
                .map(meta -> meta.group)
                .toList();
    }

    @Transactional
    public UserGroup createGroup(final UserGroup group) {
        group.persist();
        return group;
    }

    @Transactional
    public UserGroup updateGroup(final UserGroup group) {
        final UserGroup existingGroup = UserGroup.findById(group.id);
        if (existingGroup == null) {
            throw new WebApplicationException("Group not found", Response.Status.NOT_FOUND);
        }

        // Complete replacement (PUT semantics)
        existingGroup.name = group.name;

        existingGroup.persist();
        return existingGroup;
    }

    @Transactional
    public UserGroup patchGroup(final UserGroup group) {
        final UserGroup existingGroup = UserGroup.findById(group.id);
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
        return UserGroup.deleteById(id);
    }

    @Transactional
    public UserGroupMeta addUserToGroup(final Long userId, final Long groupId) {
        // Check if association already exists
        if (UserGroupMeta.isUserInGroup(userId, groupId)) {
            throw new WebApplicationException("User is already in this group", Response.Status.CONFLICT);
        }

        final User user = User.findById(userId);
        final UserGroup group = UserGroup.findById(groupId);

        if (user == null) {
            throw new WebApplicationException("User not found", Response.Status.NOT_FOUND);
        }
        if (group == null) {
            throw new WebApplicationException("Group not found", Response.Status.NOT_FOUND);
        }

        final var meta = new UserGroupMeta();
        meta.user = user;
        meta.group = group;
        meta.timestamp = LocalDateTime.now();
        meta.persist();

        return meta;
    }

    @Transactional
    public boolean removeUserFromGroup(final Long userId, final Long groupId) {
        final var meta = UserGroupMeta.findByUserAndGroup(userId, groupId);
        if (meta == null) {
            return false;
        }
        meta.delete();
        return true;
    }

    public boolean isUserInGroup(final Long userId, final Long groupId) {
        return UserGroupMeta.isUserInGroup(userId, groupId);
    }
}
