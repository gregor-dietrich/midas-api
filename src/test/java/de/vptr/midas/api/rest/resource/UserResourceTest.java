package de.vptr.midas.api.rest.resource;

import static de.vptr.midas.api.util.TestDataBuilder.createDefaultUserUpdateJson;
import static de.vptr.midas.api.util.TestDataBuilder.createUniqueUserJson;
import static de.vptr.midas.api.util.TestUtil.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

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
    void testGetAllUsers_authorizedButInsufficientRole() {
        // @formatter:off
        given()
            .auth().basic("admin", "admin")
        .when()
            .get(ENDPOINT_URL)
        .then()
            .statusCode(anyOf(is(200), is(403))); // 403 if no user:delete or user:edit role
        // @formatter:on
    }

    @Test
    void testGetCurrentUser_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL + "/me");
    }

    @Test
    void testGetCurrentUser_authorized() {
        testAuthorizedGetWithOptionalResource(ENDPOINT_URL + "/me");
    }

    @Test
    void testCreateUser_unauthorized() {
        testUnauthorizedPost(ENDPOINT_URL, createUniqueUserJson());
    }

    @Test
    @TestTransaction
    void testCreateUser_authorizedButInsufficientRole() {
        testAuthorizedPostWithRoleCheck(ENDPOINT_URL, createUniqueUserJson());
    }

    @Test
    void testGetUserByUsername_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL + "/username/admin");
    }

    @Test
    void testGetUserByUsername_authorized() {
        testAuthorizedGetWithOptionalResource(ENDPOINT_URL + "/username/admin");
    }

    @Test
    void testGetUserById_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL + "/1");
    }

    @Test
    void testGetUserById_authorized() {
        testAuthorizedGetWithOptionalResource(ENDPOINT_URL + "/1");
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
