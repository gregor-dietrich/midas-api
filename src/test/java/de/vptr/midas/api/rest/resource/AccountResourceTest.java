package de.vptr.midas.api.rest.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class AccountResourceTest {
    private static final String ENDPOINT_URL = "/api/v1/accounts";

    @Test
    void testGetAllUserAccounts_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL)
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetAllUserAccounts_authorized() {
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
    void testGetUserAccountById_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetUserAccountById_authorized() {
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
    void testCreateUserAccount_unauthorized() {
        final String accountJson = """
                {
                    "name": "Test Account"
                }
                """;
        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(accountJson)
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testCreateUserAccount_authorized() {
        final String accountJson = """
                {
                    "name": "Test Account"
                }
                """;
        // @formatter:off
        given()
            .auth().basic("admin", "admin")
            .contentType(ContentType.JSON)
            .body(accountJson)
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(is(201));
        // @formatter:on
    }

    @Test
    void testCreateUserAccount_forbidden() {
        final String accountJson = """
                {
                    "name": "Test Account"
                }
                """;
        // @formatter:off
        given()
            .auth().basic("user", "user")
            .contentType(ContentType.JSON)
            .body(accountJson)
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(is(403));
        // @formatter:on
    }

    @Test
    void testUpdateUserAccount_unauthorized() {
        final String accountJson = """
                {
                    "name": "Updated Account Name"
                }
                """;
        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(accountJson)
        .when()
            .put(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testDeleteUserAccount_unauthorized() {
        // @formatter:off
        given()
        .when()
            .delete(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }
}
