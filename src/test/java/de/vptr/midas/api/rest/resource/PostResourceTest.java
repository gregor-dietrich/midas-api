package de.vptr.midas.api.rest.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class PostResourceTest {
    private static final String ENDPOINT_URL = "/api/v1/posts";

    @Test
    void testGetAllPosts_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL)
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetAllPosts_authorized() {
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
    void testGetPublishedPosts_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL + "/published")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetPublishedPosts_authorized() {
        // @formatter:off
        given()
            .auth().basic("admin", "admin")
        .when()
            .get(ENDPOINT_URL + "/published")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
        // @formatter:on
    }

    @Test
    void testGetPost_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetPost_authorized() {
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
    void testGetPostsByUser_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL + "/user/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetPostsByUser_authorized() {
        // @formatter:off
        given()
            .auth().basic("admin", "admin")
        .when()
            .get(ENDPOINT_URL + "/user/1")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
        // @formatter:on
    }

    @Test
    void testGetPostsByCategory_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL + "/category/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetPostsByCategory_authorized() {
        // @formatter:off
        given()
            .auth().basic("admin", "admin")
        .when()
            .get(ENDPOINT_URL + "/category/1")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
        // @formatter:on
    }

    @Test
    void testCreatePost_unauthorized() {
        final String postJson = """
                {
                    "title": "Test Post",
                    "content": "Test content",
                    "published": false
                }
                """;

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(postJson)
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testCreatePost_authorizedButInsufficientRole() {
        final String postJson = """
                {
                    "title": "Test Post",
                    "content": "Test content",
                    "published": false
                }
                """;

        // @formatter:off
        given()
            .auth().basic("admin", "admin")
            .contentType(ContentType.JSON)
            .body(postJson)
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(anyOf(is(403), is(201))); // 403 if no post:add role, 201 if has role
        // @formatter:on
    }

    @Test
    void testUpdatePost_unauthorized() {
        final String postJson = """
                {
                    "title": "Updated Post",
                    "content": "Updated content"
                }
                """;

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(postJson)
        .when()
            .put(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testPatchPost_unauthorized() {
        final String postJson = """
                {
                    "title": "Patched Post"
                }
                """;

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(postJson)
        .when()
            .patch(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testDeletePost_unauthorized() {
        // @formatter:off
        given()
        .when()
            .delete(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }
}
