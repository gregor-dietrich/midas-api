package de.vptr.midas.api.security;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import de.vptr.midas.api.rest.service.UserRankService;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class UserRankIdentityAugmentorTest {

    @Inject
    UserIdentityProvider userIdentityProvider;

    @Inject
    UserRankIdentityAugmentor userRankIdentityAugmentor;

    @Inject
    UserRankService userRankService;

    @Test
    void testBuildRolesFromUserRank_Administrator() {
        // given
        final var rankName = "Administrator";

        // when
        final var roles = this.getRolesByUserRank(rankName);

        // then
        assertNotNull(roles);
        assertFalse(roles.isEmpty());

        assertTrue(roles.contains("post:add"));
        assertTrue(roles.contains("post:delete"));
        assertTrue(roles.contains("post:edit"));

        assertTrue(roles.contains("post-category:add"));
        assertTrue(roles.contains("post-category:delete"));
        assertTrue(roles.contains("post-category:edit"));

        assertTrue(roles.contains("post-comment:add"));
        assertTrue(roles.contains("post-comment:delete"));
        assertTrue(roles.contains("post-comment:edit"));

        assertTrue(roles.contains("user:add"));
        assertTrue(roles.contains("user:delete"));
        assertTrue(roles.contains("user:edit"));

        assertTrue(roles.contains("user-group:add"));
        assertTrue(roles.contains("user-group:delete"));
        assertTrue(roles.contains("user-group:edit"));

        assertTrue(roles.contains("user-rank:add"));
        assertTrue(roles.contains("user-rank:delete"));
        assertTrue(roles.contains("user-rank:edit"));
    }

    @Test
    void testBuildRolesFromUserRank_Moderator() {
        // given
        final var rankName = "Moderator";

        // when
        final var roles = this.getRolesByUserRank(rankName);

        // then
        assertNotNull(roles);
        assertFalse(roles.isEmpty());

        assertFalse(roles.contains("post:add"));
        assertFalse(roles.contains("post:delete"));
        assertFalse(roles.contains("post:edit"));

        assertFalse(roles.contains("post-category:add"));
        assertFalse(roles.contains("post-category:delete"));
        assertFalse(roles.contains("post-category:edit"));

        assertTrue(roles.contains("post-comment:add"));
        assertTrue(roles.contains("post-comment:delete"));
        assertTrue(roles.contains("post-comment:edit"));

        assertFalse(roles.contains("user:add"));
        assertFalse(roles.contains("user:delete"));
        assertFalse(roles.contains("user:edit"));

        assertFalse(roles.contains("user-group:add"));
        assertFalse(roles.contains("user-group:delete"));
        assertFalse(roles.contains("user-group:edit"));

        assertFalse(roles.contains("user-rank:add"));
        assertFalse(roles.contains("user-rank:delete"));
        assertFalse(roles.contains("user-rank:edit"));
    }

    @Test
    void testBuildRolesFromUserRank_User() {
        // given
        final var rankName = "User";

        // when
        final var roles = this.getRolesByUserRank(rankName);

        // then
        assertNotNull(roles);
        assertFalse(roles.isEmpty());

        assertFalse(roles.contains("post:add"));
        assertFalse(roles.contains("post:delete"));
        assertFalse(roles.contains("post:edit"));

        assertFalse(roles.contains("post-category:add"));
        assertFalse(roles.contains("post-category:delete"));
        assertFalse(roles.contains("post-category:edit"));

        assertTrue(roles.contains("post-comment:add"));
        assertFalse(roles.contains("post-comment:delete"));
        assertFalse(roles.contains("post-comment:edit"));

        assertFalse(roles.contains("user:add"));
        assertFalse(roles.contains("user:delete"));
        assertFalse(roles.contains("user:edit"));

        assertFalse(roles.contains("user-group:add"));
        assertFalse(roles.contains("user-group:delete"));
        assertFalse(roles.contains("user-group:edit"));

        assertFalse(roles.contains("user-rank:add"));
        assertFalse(roles.contains("user-rank:delete"));
        assertFalse(roles.contains("user-rank:edit"));
    }

    @Test
    void testBuildRolesFromUserRank_invalidRank() {
        // given
        final var rankName = "InvalidRank";

        // when & then
        assertThrows(NullPointerException.class, () -> this.getRolesByUserRank(rankName));
    }

    @Test
    void testAugmentIdentity_admin() {
        // given
        final var username = "admin";
        final var password = "admin";

        // when
        final var identity = this.getAugmentedIdentity(username, password);

        // then
        assertNotNull(identity);
        assertNotNull(identity.getRoles());
        assertFalse(identity.getRoles().isEmpty());

        assertTrue(identity.getRoles().contains("post:add"));
        assertTrue(identity.getRoles().contains("post:delete"));
        assertTrue(identity.getRoles().contains("post:edit"));

        assertTrue(identity.getRoles().contains("post-category:add"));
        assertTrue(identity.getRoles().contains("post-category:delete"));
        assertTrue(identity.getRoles().contains("post-category:edit"));

        assertTrue(identity.getRoles().contains("post-comment:add"));
        assertTrue(identity.getRoles().contains("post-comment:delete"));
        assertTrue(identity.getRoles().contains("post-comment:edit"));

        assertTrue(identity.getRoles().contains("user:add"));
        assertTrue(identity.getRoles().contains("user:delete"));
        assertTrue(identity.getRoles().contains("user:edit"));

        assertTrue(identity.getRoles().contains("user-group:add"));
        assertTrue(identity.getRoles().contains("user-group:delete"));
        assertTrue(identity.getRoles().contains("user-group:edit"));

        assertTrue(identity.getRoles().contains("user-rank:add"));
        assertTrue(identity.getRoles().contains("user-rank:delete"));
        assertTrue(identity.getRoles().contains("user-rank:edit"));
    }

    @Test
    void testAugmentIdentity_moderator() {
        // given
        final var username = "moderator";
        final var password = "moderator";

        // when
        final var identity = this.getAugmentedIdentity(username, password);

        // then
        assertNotNull(identity);
        assertNotNull(identity.getRoles());
        assertFalse(identity.getRoles().isEmpty());

        assertFalse(identity.getRoles().contains("post:add"));
        assertFalse(identity.getRoles().contains("post:delete"));
        assertFalse(identity.getRoles().contains("post:edit"));

        assertFalse(identity.getRoles().contains("post-category:add"));
        assertFalse(identity.getRoles().contains("post-category:delete"));
        assertFalse(identity.getRoles().contains("post-category:edit"));

        assertTrue(identity.getRoles().contains("post-comment:add"));
        assertTrue(identity.getRoles().contains("post-comment:delete"));
        assertTrue(identity.getRoles().contains("post-comment:edit"));

        assertFalse(identity.getRoles().contains("user:add"));
        assertFalse(identity.getRoles().contains("user:delete"));
        assertFalse(identity.getRoles().contains("user:edit"));

        assertFalse(identity.getRoles().contains("user-group:add"));
        assertFalse(identity.getRoles().contains("user-group:delete"));
        assertFalse(identity.getRoles().contains("user-group:edit"));

        assertFalse(identity.getRoles().contains("user-rank:add"));
        assertFalse(identity.getRoles().contains("user-rank:delete"));
        assertFalse(identity.getRoles().contains("user-rank:edit"));
    }

    @Test
    void testAugmentIdentity_user() {
        // given
        final var username = "user";
        final var password = "user";

        // when
        final var identity = this.getAugmentedIdentity(username, password);

        // then
        assertNotNull(identity);
        assertNotNull(identity.getRoles());
        assertFalse(identity.getRoles().isEmpty());

        assertFalse(identity.getRoles().contains("post:add"));
        assertFalse(identity.getRoles().contains("post:delete"));
        assertFalse(identity.getRoles().contains("post:edit"));

        assertFalse(identity.getRoles().contains("post-category:add"));
        assertFalse(identity.getRoles().contains("post-category:delete"));
        assertFalse(identity.getRoles().contains("post-category:edit"));

        assertTrue(identity.getRoles().contains("post-comment:add"));
        assertFalse(identity.getRoles().contains("post-comment:delete"));
        assertFalse(identity.getRoles().contains("post-comment:edit"));

        assertFalse(identity.getRoles().contains("user:add"));
        assertFalse(identity.getRoles().contains("user:delete"));
        assertFalse(identity.getRoles().contains("user:edit"));

        assertFalse(identity.getRoles().contains("user-group:add"));
        assertFalse(identity.getRoles().contains("user-group:delete"));
        assertFalse(identity.getRoles().contains("user-group:edit"));

        assertFalse(identity.getRoles().contains("user-rank:add"));
        assertFalse(identity.getRoles().contains("user-rank:delete"));
        assertFalse(identity.getRoles().contains("user-rank:edit"));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "nonExistingUser,passwordDoesNotMatter",
            "bannedUser,bannedUser",
            "notActivatedUser,notActivatedUser",
            ",",
            " , ",
            "null,null"
    }, nullValues = "null")
    void testAugmentIdentity_fails(final String username, final String password) {
        assertThrows(AuthenticationFailedException.class, () -> this.getAugmentedIdentity(username, password));
    }

    private Set<String> getRolesByUserRank(final String rankName) {
        final var rank = this.userRankService.findByName(rankName).orElse(null);
        return this.userRankIdentityAugmentor.buildRolesFromUserRank(rank);
    }

    private SecurityIdentity getAugmentedIdentity(final String username, final String password) {
        final var identity = this.userIdentityProvider.authenticateUser(username, password);
        return this.userRankIdentityAugmentor.augmentIdentity(identity, username);
    }
}