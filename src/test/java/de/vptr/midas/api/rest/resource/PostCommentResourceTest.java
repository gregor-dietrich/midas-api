package de.vptr.midas.api.rest.resource;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

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
            .statusCode(200);
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
            .statusCode(200);
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

    private Long createTestCategory() {
        final String categoryJson = """
                {
                    \"name\": \"Test Category\",
                    \"description\": \"Test description\"
                }
                """;
        final Response response = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(categoryJson)
                .when()
                .post("/api/v1/categories");
        final Integer id = response.then().statusCode(201).extract().path("id");
        return id != null ? id.longValue() : null;
    }

    private Long createTestPost() {
        final Long categoryId = this.createTestCategory();
        final String postJson = String.format("""
                {
                    \"title\": \"Test Post\",
                    \"content\": \"Test content\",
                    \"published\": true,
                    \"commentable\": true,
                    \"userId\": 1,
                    \"categoryId\": %d
                }
                """, categoryId);
        final Response response = given()
                .auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(postJson)
                .when()
                .post("/api/v1/posts");
        final Integer id = response.then().statusCode(201).extract().path("id");
        return id != null ? id.longValue() : null;
    }

    @Test
    void testCreateComment_authorized() {
        final Long postId = this.createTestPost();
        final String commentJson = String.format("""
                {
                    \"content\": \"Test comment\",
                    \"postId\": %d
                }
                """, postId);
        // @formatter:off
        given()
            .auth().basic("admin", "admin")
            .contentType(ContentType.JSON)
            .body(commentJson)
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(201);
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
