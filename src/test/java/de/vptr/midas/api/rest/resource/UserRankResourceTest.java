package de.vptr.midas.api.rest.resource;

import static de.vptr.midas.api.util.TestDataBuilder.createDefaultUserRankJson;
import static de.vptr.midas.api.util.TestDataBuilder.createUpdatedUserRankJson;
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
    void testGetUserRankById_authorized() {
        testAuthorizedGetWithOptionalResource(ENDPOINT_URL + "/1");
    }

    @Test
    void testCreateUserRank_unauthorized() {
        testUnauthorizedPost(ENDPOINT_URL, createDefaultUserRankJson());
    }

    @Test
    void testCreateUserRank_authorizedButInsufficientRole() {
        testAuthorizedPostWithRoleCheck(ENDPOINT_URL, createDefaultUserRankJson());
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
