package de.vptr.midas.api.rest.resource;

import static de.vptr.midas.api.util.TestDataBuilder.*;
import static de.vptr.midas.api.util.TestUtil.*;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class UserRankResourceTest {
    private static final String ENDPOINT_URL = "/api/v1/user-ranks";

    @Test
    void testGetAllUserRanks_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL);
    }

    @Test
    void testGetAllUserRanks_authorized() {
        testAuthorizedGetWithJson(ENDPOINT_URL);
    }

    @Test
    void testGetUserRankById_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL + "/1");
    }

    @Test
    void testGetUserRankById_authorizedWithExistingRank() {
        testAuthorizedGetWithExistingResource(ENDPOINT_URL + "/1");
    }

    @Test
    void testGetUserRankById_authorizedWithNonExistentRank() {
        testAuthorizedGetWithNonExistentResource(ENDPOINT_URL + "/999");
    }

    @Test
    void testCreateUserRank_unauthorized() {
        testUnauthorizedPost(ENDPOINT_URL, createDefaultUserRankJson());
    }

    @Test
    void testCreateUserRank_authorizedWithSufficientRole() {
        testAuthorizedPostWithCreation(ENDPOINT_URL, createDefaultUserRankJson());
    }

    @Test
    void testCreateUserRank_authorizedWithInsufficientRole() {
        testAuthorizedPostWithInsufficientRole(ENDPOINT_URL, createDefaultUserRankJson());
    }

    @Test
    void testCreateUserRank_authorizedWithEmptyJson() {
        testAuthorizedPostWithValidationError(ENDPOINT_URL, createEmptyJson());
    }

    @Test
    void testCreateUserRank_authorizedWithInvalidJson() {
        testAuthorizedPostWithValidationError(ENDPOINT_URL, createInvalidJson());
    }

    @Test
    void testCreateUserRank_authorizedWithMalformedJson() {
        testAuthorizedPostWithSyntaxError(ENDPOINT_URL, createMalformedJson());
    }

    @Test
    void testUpdateUserRank_unauthorized() {
        testUnauthorizedPut(ENDPOINT_URL + "/1", createUpdatedUserRankJson());
    }

    @Test
    void testDeleteUserRank_unauthorized() {
        testUnauthorizedDelete(ENDPOINT_URL + "/1");
    }
}
