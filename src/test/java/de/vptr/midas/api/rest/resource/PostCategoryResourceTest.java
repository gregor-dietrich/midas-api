package de.vptr.midas.api.rest.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class PostCategoryResourceTest {
    private static final String ENDPOINT_URL = "/api/v1/categories";

    @Test
    void testGetAllPostCategories_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL)
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetAllPostCategories_authorized() {
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
    void testGetRootCategories_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL + "/root")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetRootCategories_authorized() {
        // @formatter:off
        given()
            .auth().basic("admin", "admin")
        .when()
            .get(ENDPOINT_URL + "/root")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
        // @formatter:on
    }

    @Test
    void testGetCategoryById_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetCategoryById_authorized() {
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
    void testGetCategoriesByParent_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL + "/parent/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetCategoriesByParent_authorized() {
        // @formatter:off
        given()
            .auth().basic("admin", "admin")
        .when()
            .get(ENDPOINT_URL + "/parent/1")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
        // @formatter:on
    }

    @Test
    void testCreateCategory_unauthorized() {
        final String categoryJson = """
                {
                    "name": "Test Category",
                    "description": "Test description"
                }
                """;

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(categoryJson)
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testCreateCategory_authorizedButInsufficientRole() {
        final String categoryJson = """
                {
                    "name": "Test Category",
                    "description": "Test description"
                }
                """;

        // @formatter:off
        given()
            .auth().basic("admin", "admin")
            .contentType(ContentType.JSON)
            .body(categoryJson)
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(anyOf(is(201), is(403))); // 403 if no category:add role
        // @formatter:on
    }

    @Test
    void testUpdateCategory_unauthorized() {
        final String categoryJson = """
                {
                    "name": "Updated Category",
                    "description": "Updated description"
                }
                """;

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(categoryJson)
        .when()
            .put(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testDeleteCategory_unauthorized() {
        // @formatter:off
        given()
        .when()
            .delete(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }
}
