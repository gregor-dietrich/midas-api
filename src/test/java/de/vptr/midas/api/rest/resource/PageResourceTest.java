package de.vptr.midas.api.rest.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class PageResourceTest {
    private static final String ENDPOINT_URL = "/api/v1/pages";

    @Test
    void testGetAllPages_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL)
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetAllPages_authorized() {
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
    void testGetPageById_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetPageById_authorized() {
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
    void testCreatePage_unauthorized() {
        final String pageJson = """
                {
                    "title": "Test Page",
                    "content": "Test content",
                    "published": false
                }
                """;

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(pageJson)
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testCreatePage_authorizedButInsufficientRole() {
        final String pageJson = """
                {
                    "title": "Test Page",
                    "content": "Test content",
                    "published": false
                }
                """;

        // @formatter:off
        given()
            .auth().basic("admin", "admin")
            .contentType(ContentType.JSON)
            .body(pageJson)
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(anyOf(is(201), is(403))); // 403 if no page:add role
        // @formatter:on
    }

    @Test
    void testUpdatePage_unauthorized() {
        final String pageJson = """
                {
                    "title": "Updated Page",
                    "content": "Updated content"
                }
                """;

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(pageJson)
        .when()
            .put(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testDeletePage_unauthorized() {
        // @formatter:off
        given()
        .when()
            .delete(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }
}
