package de.vptr.midas.api.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class UserIdentityProviderTest {

    @Inject
    UserIdentityProvider userIdentityProvider;

    @Test
    void testGetRequestType() {
        assertEquals(UsernamePasswordAuthenticationRequest.class, this.userIdentityProvider.getRequestType());
    }

    @Test
    void testAuthenticateSuccess() {
        // given
        final var username = "admin";
        final var password = "admin";

        // when
        final var identity = this.userIdentityProvider.authenticateUser(username, password);

        // then
        assertFalse(identity.isAnonymous());
        assertEquals("admin", identity.getPrincipal().getName());
    }

    @Test
    void testAuthenticateUserNotFound() {
        // given
        final var username = "nonexistent";
        final var password = "password";

        // when
        try {
            this.userIdentityProvider.authenticateUser(username, password);
        } catch (final Exception e) {
            // then
            assertEquals("Invalid credentials", e.getMessage());
        }
    }

    @Test
    void testAuthenticateInvalidPassword() {
        // given
        final var username = "admin";
        final var password = "wrongpassword";

        // when
        try {
            this.userIdentityProvider.authenticateUser(username, password);
        } catch (final Exception e) {
            // then
            assertEquals("Invalid credentials", e.getMessage());
        }
    }

    @Test
    void testAuthenticateBannedUser() {
        // given
        final var username = "bannedUser";
        final var password = "bannedUser";

        // when
        try {
            this.userIdentityProvider.authenticateUser(username, password);
        } catch (final Exception e) {
            // then
            assertEquals("User is banned", e.getMessage());
        }

    }

    @Test
    void testAuthenticateNotActivatedUser() {
        // given
        final var username = "notActivatedUser";
        final var password = "notActivatedUser";

        // when
        try {
            this.userIdentityProvider.authenticateUser(username, password);
        } catch (final Exception e) {
            // then
            assertEquals("User is not activated", e.getMessage());
        }
    }
}