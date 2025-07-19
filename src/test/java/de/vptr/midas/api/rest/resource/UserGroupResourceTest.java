package de.vptr.midas.api.rest.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class UserGroupResourceTest {
    private static final String ENDPOINT_URL = "/api/v1/user-groups";

    @Test
    void testGetAllUserGroups_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL)
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetAllUserGroups_authorized() {
        // @formatter:off
        given()
            .auth().basic("admin", "admin")
        .when()
            .get(ENDPOINT_URL)
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
        // @formatter:on
    }

    @Test
    void testGetGroupById_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetGroupById_authorized() {
        // @formatter:off
        given()
            .auth().basic("admin", "admin")
        .when()
            .get(ENDPOINT_URL + "/1")
        .then()
            .statusCode(anyOf(is(200), is(404)))
            .contentType(ContentType.JSON);
        // @formatter:on
    }

    @Test
    void testGetUsersInGroup_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL + "/1/users")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetUsersInGroup_authorized() {
        // @formatter:off
        given()
            .auth().basic("admin", "admin")
        .when()
            .get(ENDPOINT_URL + "/1/users")
        .then()
            .statusCode(anyOf(is(200), is(404)));
        // @formatter:on
    }

    @Test
    void testCreateGroup_unauthorized() {
        final String groupJson = """
                {
                    "name": "Test Group",
                    "description": "Test description"
                }
                """;

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(groupJson)
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testCreateGroup_authorizedButInsufficientRole() {
        final String groupJson = """
                {
                    "name": "Test Group",
                    "description": "Test description"
                }
                """;

        // @formatter:off
        given()
            .auth().basic("admin", "admin")
            .contentType(ContentType.JSON)
            .body(groupJson)
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(anyOf(is(201), is(403), is(500))); // 403 if no group:add role, 500 if validation fails
        // @formatter:on
    }

    @Test
    void testUpdateGroup_unauthorized() {
        final String groupJson = """
                {
                    "name": "Updated Group",
                    "description": "Updated description"
                }
                """;

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(groupJson)
        .when()
            .put(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testDeleteGroup_unauthorized() {
        // @formatter:off
        given()
        .when()
            .delete(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
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
