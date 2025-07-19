package de.vptr.midas.api.rest.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class PostCommentResourceTest {
    private static final String ENDPOINT_URL = "/api/v1/comments";

    @Test
    void testGetAllPostComments_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL)
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetAllPostComments_authorized() {
        // @formatter:off
        given()
            .auth().basic("admin", "admin")
        .when()
            .get(ENDPOINT_URL)
        .then()
            .statusCode(anyOf(is(200), is(403)));
        // @formatter:on
    }

    @Test
    void testGetCommentById_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetCommentById_authorized() {
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
    void testGetCommentsByPost_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL + "/post/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetCommentsByPost_authorized() {
        // @formatter:off
        given()
            .auth().basic("admin", "admin")
        .when()
            .get(ENDPOINT_URL + "/post/1")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
        // @formatter:on
    }

    @Test
    void testCreateComment_unauthorized() {
        final String commentJson = """
                {
                    "content": "Test comment",
                    "postId": 1
                }
                """;

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(commentJson)
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testCreateComment_authorizedButInsufficientRole() {
        final String commentJson = """
                {
                    "content": "Test comment",
                    "postId": 1
                }
                """;

        // @formatter:off
        given()
            .auth().basic("admin", "admin")
            .contentType(ContentType.JSON)
            .body(commentJson)
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(anyOf(is(201), is(403))); // 403 if no comment:add role
        // @formatter:on
    }

    @Test
    void testUpdateComment_unauthorized() {
        final String commentJson = """
                {
                    "content": "Updated comment"
                }
                """;

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(commentJson)
        .when()
            .put(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testDeleteComment_unauthorized() {
        // @formatter:off
        given()
        .when()
            .delete(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }
}
