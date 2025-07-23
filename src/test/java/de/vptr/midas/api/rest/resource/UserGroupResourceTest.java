package de.vptr.midas.api.rest.resource;

import static de.vptr.midas.api.util.TestDataBuilder.createDefaultUserGroupJson;
import static de.vptr.midas.api.util.TestDataBuilder.createUpdatedUserGroupJson;
import static de.vptr.midas.api.util.TestUtil.*;
import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class UserGroupResourceTest {
    private static final String ENDPOINT_URL = "/api/v1/user-groups";

    @Test
    void testGetAllUserGroups_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL);
    }

    @Test
    void testGetAllUserGroups_authorized() {
        testAuthorizedGetWithJson(ENDPOINT_URL);
    }

    @Test
    void testGetGroupById_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL + "/1");
    }

    @Test
    void testGetGroupById_authorizedWithExistingGroup() {
        // TODO: expect 200 - should return existing group data
    }

    @Test
    void testGetGroupById_authorizedWithNonExistentGroup() {
        testAuthorizedGetWithNonExistentResource(ENDPOINT_URL + "/999");
    }

    @Test
    void testGetUsersInGroup_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL + "/1/users");
    }

    @Test
    void testGetUsersInGroup_authorizedWithExistingGroup() {
        testAuthorizedGetWithExistingResource(ENDPOINT_URL + "/1/users");
    }

    @Test
    void testGetUsersInGroup_authorizedWithNonExistentGroup() {
        testAuthorizedGetWithNonExistentResource(ENDPOINT_URL + "/999/users");
    }

    @Test
    void testCreateGroup_unauthorized() {
        testUnauthorizedPost(ENDPOINT_URL, createDefaultUserGroupJson());
    }

    @Test
    void testCreateGroup_authorizedWithSufficientRole() {
        testAuthorizedPostWithCreation(ENDPOINT_URL, createDefaultUserGroupJson());
    }

    @Test
    void testCreateGroup_authorizedWithInsufficientRole() {
        testAuthorizedPostWithInsufficientRole(ENDPOINT_URL, createDefaultUserGroupJson());
    }

    @Test
    void testCreateGroup_authorizedWithValidationError() {
        // TODO: expect 400 - should return validation error for invalid group data
    }

    @Test
    void testUpdateGroup_unauthorized() {
        testUnauthorizedPut(ENDPOINT_URL + "/1", createUpdatedUserGroupJson());
    }

    @Test
    void testDeleteGroup_unauthorized() {
        testUnauthorizedDelete(ENDPOINT_URL + "/1");
    }

    @Test
    void testAddUserToGroup_unauthorized() {
        // @formatter:off
        given()
            .contentType(ContentType.JSON)
        .when()
            .post(ENDPOINT_URL + "/1/users/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testRemoveUserFromGroup_unauthorized() {
        // @formatter:off
        given()
        .when()
            .delete(ENDPOINT_URL + "/1/users/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }
}
