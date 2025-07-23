package de.vptr.midas.api.rest.resource;

import static de.vptr.midas.api.util.TestDataBuilder.createDefaultUserUpdateJson;
import static de.vptr.midas.api.util.TestDataBuilder.createUniqueUserJson;
import static de.vptr.midas.api.util.TestUtil.*;

import org.junit.jupiter.api.Test;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class UserResourceTest {
    private static final String ENDPOINT_URL = "/api/v1/users";

    @Test
    void testGetAllUsers_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL);
    }

    @Test
    void testGetAllUsers_authorizedWithSufficientRole() {
        // Test assumes user has appropriate role (user:delete or user:edit)
        authenticatedRequest()
                .when()
                .get(ENDPOINT_URL)
                .then()
                .statusCode(200);
    }

    @Test
    void testGetAllUsers_authorizedWithInsufficientRole() {
        authenticatedGuestRequest()
                .when()
                .get(ENDPOINT_URL)
                .then()
                .statusCode(403);
    }

    @Test
    void testGetCurrentUser_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL + "/me");
    }

    @Test
    void testGetCurrentUser_authorized() {
        testAuthorizedGetWithExistingResource(ENDPOINT_URL + "/me");
    }

    @Test
    void testCreateUser_unauthorized() {
        testUnauthorizedPost(ENDPOINT_URL, createUniqueUserJson());
    }

    @Test
    @TestTransaction
    void testCreateUser_authorizedWithSufficientRole() {
        testAuthorizedPostWithCreation(ENDPOINT_URL, createUniqueUserJson());
    }

    @Test
    @TestTransaction
    void testCreateUser_authorizedWithInsufficientRole() {
        testAuthorizedPostWithInsufficientRole(ENDPOINT_URL, createUniqueUserJson());
    }

    @Test
    void testGetUserByUsername_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL + "/username/admin");
    }

    @Test
    void testGetUserByUsername_authorizedWithExistingUser() {
        testAuthorizedGetWithExistingResource(ENDPOINT_URL + "/username/admin");
    }

    @Test
    void testGetUserByUsername_authorizedWithNonExistentUser() {
        testAuthorizedGetWithNonExistentResource(ENDPOINT_URL + "/username/nonexistent_user");
    }

    @Test
    void testGetUserById_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL + "/1");
    }

    @Test
    void testGetUserById_authorizedWithExistingUser() {
        testAuthorizedGetWithExistingResource(ENDPOINT_URL + "/1");
    }

    @Test
    void testGetUserById_authorizedWithNonExistentUser() {
        testAuthorizedGetWithNonExistentResource(ENDPOINT_URL + "/999");
    }

    @Test
    void testUpdateUser_unauthorized() {
        testUnauthorizedPut(ENDPOINT_URL + "/1", createDefaultUserUpdateJson());
    }

    @Test
    void testDeleteUser_unauthorized() {
        testUnauthorizedDelete(ENDPOINT_URL + "/1");
    }
}
