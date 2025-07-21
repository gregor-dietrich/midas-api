package de.vptr.midas.api.rest.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class UserResourceTest {
    private static final String ENDPOINT_URL = "/api/v1/users";

    @Test
    void testGetAllUsers_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL)
        .then()
            .statusCode(401);
        // @formatter:on
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
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL + "/me")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetCurrentUser_authorized() {
        // @formatter:off
        given()
            .auth().basic("admin", "admin")
        .when()
            .get(ENDPOINT_URL + "/me")
        .then()
            .statusCode(anyOf(is(200), is(404)));
        // @formatter:on
    }

    @Test
    void testCreateUser_unauthorized() {
        final String userJson = """
                {
                    "username": "testuser",
                    "email": "test@example.com",
                    "password": "password123"
                }
                """;

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(userJson)
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    @TestTransaction
    void testCreateUser_authorizedButInsufficientRole() {
        // Generate unique username to avoid conflicts
        final String uniqueSuffix = String.valueOf(System.currentTimeMillis() + (int) (Math.random() * 10000));
        final String userJson = String.format("""
                {
                    "username": "testuser_%s",
                    "email": "test_%s@example.com",
                    "password": "password123"
                }
                """, uniqueSuffix, uniqueSuffix);

        // @formatter:off
        given()
            .auth().basic("admin", "admin")
            .contentType(ContentType.JSON)
            .body(userJson)
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(anyOf(is(201), is(403))); // 403 if no user:add role
        // @formatter:on
    }

    @Test
    void testGetUserByUsername_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL + "/username/admin")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetUserByUsername_authorized() {
        // @formatter:off
        given()
            .auth().basic("admin", "admin")
        .when()
            .get(ENDPOINT_URL + "/username/admin")
        .then()
            .statusCode(anyOf(is(200), is(404)));
        // @formatter:on
    }

    @Test
    void testGetUserById_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetUserById_authorized() {
        // @formatter:off
        given()
            .auth().basic("admin", "admin")
        .when()
            .get(ENDPOINT_URL + "/1")
        .then()
            .statusCode(anyOf(is(200), is(404)));
        // @formatter:on
    }

    @Test
    void testUpdateUser_unauthorized() {
        final String userJson = """
                {
                    "username": "updateduser",
                    "email": "updated@example.com"
                }
                """;

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(userJson)
        .when()
            .put(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testDeleteUser_unauthorized() {
        // @formatter:off
        given()
        .when()
            .delete(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }
}
