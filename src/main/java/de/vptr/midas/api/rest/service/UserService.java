package de.vptr.midas.api.rest.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.entity.UserEntity;
import de.vptr.midas.api.rest.entity.UserRankEntity;
import de.vptr.midas.api.security.PasswordHashingService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class UserService {

    @Inject
    PasswordHashingService passwordHashingService;

    public List<UserEntity> getAllUsers() {
        return UserEntity.listAll();
    }

    public Optional<UserEntity> findByUsername(final String username) {
        return UserEntity.find("username", username).firstResultOptional();
    }

    public Optional<UserEntity> findByEmail(final String email) {
        return UserEntity.find("email", email).firstResultOptional();
    }

    @Transactional
    public UserEntity createUser(final UserEntity user) {
        // Validate that password is provided
        if (user.password == null || user.password.trim().isEmpty()) {
            throw new WebApplicationException("Password is required", Response.Status.BAD_REQUEST);
        }

        // Generate salt and hash password
        final var salt = this.passwordHashingService.generateSalt();
        try {
            final var hashedPassword = this.passwordHashingService.hashPassword(user.password, salt);
            user.salt = salt;
            user.password = hashedPassword;
        } catch (final Exception e) {
            throw new WebApplicationException("Failed to hash password", Response.Status.INTERNAL_SERVER_ERROR);
        }

        user.created = LocalDateTime.now();
        user.lastLogin = user.created;

        if (user.rank == null) {
            user.rank = UserRankEntity.findById(1L);
        }

        if (user.banned == null) {
            user.banned = false;
        }

        user.activated = false;
        user.activationKey = java.util.UUID.randomUUID().toString();

        user.persist();
        return user;
    }

    @Transactional
    public UserEntity updateUser(final UserEntity user) {
        final UserEntity existingUser = UserEntity.findById(user.id);
        if (existingUser == null) {
            throw new WebApplicationException("User not found", Response.Status.NOT_FOUND);
        }

        // Complete replacement (PUT semantics)
        existingUser.username = user.username;
        existingUser.email = user.email;
        existingUser.banned = user.banned != null ? user.banned : false;
        existingUser.activated = user.activated != null ? user.activated : false;
        existingUser.activationKey = user.activationKey;
        existingUser.lastIp = user.lastIp;

        // Handle password update if provided
        if (user.password != null && !user.password.trim().isEmpty()) {
            final var salt = this.passwordHashingService.generateSalt();
            try {
                final var hashedPassword = this.passwordHashingService.hashPassword(user.password, salt);
                existingUser.salt = salt;
                existingUser.password = hashedPassword;
            } catch (final Exception e) {
                throw new WebApplicationException("Failed to hash password", Response.Status.INTERNAL_SERVER_ERROR);
            }
        }

        // Keep existing rank if not provided
        if (user.rank != null) {
            existingUser.rank = user.rank;
        }

        existingUser.persist();
        return existingUser;
    }

    @Transactional
    public UserEntity patchUser(final UserEntity user) {
        final UserEntity existingUser = UserEntity.findById(user.id);
        if (existingUser == null) {
            throw new WebApplicationException("User not found", Response.Status.NOT_FOUND);
        }

        // Partial update (PATCH semantics) - only update provided fields
        if (user.username != null) {
            existingUser.username = user.username;
        }
        if (user.email != null) {
            existingUser.email = user.email;
        }
        if (user.banned != null) {
            existingUser.banned = user.banned;
        }
        if (user.activated != null) {
            existingUser.activated = user.activated;
        }
        if (user.activationKey != null) {
            existingUser.activationKey = user.activationKey;
        }
        if (user.lastIp != null) {
            existingUser.lastIp = user.lastIp;
        }
        if (user.rank != null) {
            existingUser.rank = user.rank;
        }

        // Handle password update if provided
        if (user.password != null && !user.password.trim().isEmpty()) {
            final var salt = this.passwordHashingService.generateSalt();
            try {
                final var hashedPassword = this.passwordHashingService.hashPassword(user.password, salt);
                existingUser.salt = salt;
                existingUser.password = hashedPassword;
            } catch (final Exception e) {
                throw new WebApplicationException("Failed to hash password", Response.Status.INTERNAL_SERVER_ERROR);
            }
        }

        existingUser.persist();
        return existingUser;
    }

    @Transactional
    public boolean deleteUser(final Long id) {
        return UserEntity.deleteById(id);
    }

    public List<UserEntity> findActiveUsers() {
        return UserEntity.find("activated = true and banned = false").list();
    }
}