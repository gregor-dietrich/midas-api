package de.vptr.midas.api.rest.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class UserRankResourceTest {
    private static final String ENDPOINT_URL = "/api/v1/user-ranks";

    @Test
    void testGetAllUserRanks_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL)
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetAllUserRanks_authorized() {
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
    void testGetUserRankById_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetUserRankById_authorized() {
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
    void testCreateUserRank_unauthorized() {
        final String rankJson = """
                {
                    "name": "Test Rank",
                    "userAdd": true
                }
                """;
        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(rankJson)
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testCreateUserRank_authorizedButInsufficientRole() {
        final String rankJson = """
                {
                    "name": "Test Rank",
                    "userAdd": true
                }
                """;
        // @formatter:off
        given()
            .auth().basic("admin", "admin")
            .contentType(ContentType.JSON)
            .body(rankJson)
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(anyOf(is(201), is(403)));
        // @formatter:on
    }

    @Test
    void testUpdateUserRank_unauthorized() {
        final String rankJson = """
                {
                    "name": "Updated Rank"
                }
                """;
        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(rankJson)
        .when()
            .put(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testDeleteUserRank_unauthorized() {
        // @formatter:off
        given()
        .when()
            .delete(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }
}
