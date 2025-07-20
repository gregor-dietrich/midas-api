package de.vptr.midas.api.rest.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import de.vptr.midas.api.rest.dto.UserDto;
import de.vptr.midas.api.rest.dto.UserResponseDto;
import de.vptr.midas.api.rest.entity.UserEntity;
import de.vptr.midas.api.rest.entity.UserRankEntity;
import de.vptr.midas.api.security.PasswordHashingService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
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

    public Optional<UserEntity> findById(final Long id) {
        return UserEntity.findByIdOptional(id);
    }

    public Optional<UserEntity> findByEmail(final String email) {
        return UserEntity.find("email", email).firstResultOptional();
    }

    @Transactional
    public UserResponseDto createUser(final UserDto userDto) {
        // Validate required fields for POST
        if (userDto.username == null || userDto.username.trim().isEmpty()) {
            throw new ValidationException("Username is required for creating a user");
        }
        if (userDto.password == null || userDto.password.trim().isEmpty()) {
            throw new ValidationException("Password is required for creating a user");
        }
        if (userDto.email == null || userDto.email.trim().isEmpty()) {
            throw new ValidationException("Email is required for creating a user");
        }

        final UserEntity user = new UserEntity();
        user.username = userDto.username;
        user.email = userDto.email;
        user.banned = userDto.banned != null ? userDto.banned : false;
        user.activated = userDto.activated != null ? userDto.activated : false;
        user.activationKey = userDto.activationKey != null ? userDto.activationKey
                : java.util.UUID.randomUUID().toString();
        user.lastIp = userDto.lastIp;

        // Generate salt and hash password
        final var salt = this.passwordHashingService.generateSalt();
        try {
            final var hashedPassword = this.passwordHashingService.hashPassword(userDto.password, salt);
            user.salt = salt;
            user.password = hashedPassword;
        } catch (final Exception e) {
            throw new WebApplicationException("Failed to hash password", Response.Status.INTERNAL_SERVER_ERROR);
        }

        user.created = LocalDateTime.now();
        user.lastLogin = user.created;

        // Set rank if provided, otherwise default to rank 1
        if (userDto.rankId != null) {
            final UserRankEntity rank = UserRankEntity.findById(userDto.rankId);
            if (rank == null) {
                throw new ValidationException("Rank with ID " + userDto.rankId + " not found");
            }
            user.rank = rank;
        } else {
            user.rank = UserRankEntity.findById(1L);
        }

        user.persist();
        return new UserResponseDto(user);
    }

    @Transactional
    public UserResponseDto updateUser(final Long id, final UserDto userDto) {
        // Validate required fields for PUT
        if (userDto.username == null || userDto.username.trim().isEmpty()) {
            throw new ValidationException("Username is required for updating a user");
        }
        if (userDto.email == null || userDto.email.trim().isEmpty()) {
            throw new ValidationException("Email is required for updating a user");
        }

        final UserEntity existingUser = UserEntity.findById(id);
        if (existingUser == null) {
            throw new WebApplicationException("User not found", Response.Status.NOT_FOUND);
        }

        // Complete replacement (PUT semantics)
        existingUser.username = userDto.username;
        existingUser.email = userDto.email;
        existingUser.banned = userDto.banned != null ? userDto.banned : false;
        existingUser.activated = userDto.activated != null ? userDto.activated : false;
        existingUser.activationKey = userDto.activationKey;
        existingUser.lastIp = userDto.lastIp;

        // Handle password update if provided
        if (userDto.password != null && !userDto.password.trim().isEmpty()) {
            final var salt = this.passwordHashingService.generateSalt();
            try {
                final var hashedPassword = this.passwordHashingService.hashPassword(userDto.password, salt);
                existingUser.salt = salt;
                existingUser.password = hashedPassword;
            } catch (final Exception e) {
                throw new WebApplicationException("Failed to hash password", Response.Status.INTERNAL_SERVER_ERROR);
            }
        }

        // Set rank if provided
        if (userDto.rankId != null) {
            final UserRankEntity rank = UserRankEntity.findById(userDto.rankId);
            if (rank == null) {
                throw new ValidationException("Rank with ID " + userDto.rankId + " not found");
            }
            existingUser.rank = rank;
        } else {
            // For PUT, null rank should reset to default
            existingUser.rank = UserRankEntity.findById(1L);
        }

        existingUser.persist();
        return new UserResponseDto(existingUser);
    }

    @Transactional
    public UserResponseDto patchUser(final Long id, final UserDto userDto) {
        final UserEntity existingUser = UserEntity.findById(id);
        if (existingUser == null) {
            throw new WebApplicationException("User not found", Response.Status.NOT_FOUND);
        }

        // Partial update (PATCH semantics) - only update provided fields
        if (userDto.username != null && !userDto.username.trim().isEmpty()) {
            existingUser.username = userDto.username;
        }
        if (userDto.email != null && !userDto.email.trim().isEmpty()) {
            existingUser.email = userDto.email;
        }
        if (userDto.banned != null) {
            existingUser.banned = userDto.banned;
        }
        if (userDto.activated != null) {
            existingUser.activated = userDto.activated;
        }
        if (userDto.activationKey != null) {
            existingUser.activationKey = userDto.activationKey;
        }
        if (userDto.lastIp != null) {
            existingUser.lastIp = userDto.lastIp;
        }

        // Handle password update if provided
        if (userDto.password != null && !userDto.password.trim().isEmpty()) {
            final var salt = this.passwordHashingService.generateSalt();
            try {
                final var hashedPassword = this.passwordHashingService.hashPassword(userDto.password, salt);
                existingUser.salt = salt;
                existingUser.password = hashedPassword;
            } catch (final Exception e) {
                throw new WebApplicationException("Failed to hash password", Response.Status.INTERNAL_SERVER_ERROR);
            }
        }

        // Set rank if provided
        if (userDto.rankId != null) {
            final UserRankEntity rank = UserRankEntity.findById(userDto.rankId);
            if (rank == null) {
                throw new ValidationException("Rank with ID " + userDto.rankId + " not found");
            }
            existingUser.rank = rank;
        }

        existingUser.persist();
        return new UserResponseDto(existingUser);
    }

    @Transactional
    public boolean deleteUser(final Long id) {
        return UserEntity.deleteById(id);
    }

    public List<UserEntity> findActiveUsers() {
        return UserEntity.find("activated = true and banned = false").list();
    }
}