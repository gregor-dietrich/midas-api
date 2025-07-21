package de.vptr.midas.api.rest.resource;

import static de.vptr.midas.api.util.TestDataBuilder.createDefaultAccountJson;
import static de.vptr.midas.api.util.TestUtil.*;
import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class AccountResourceTest {
    private static final String ENDPOINT_URL = "/api/v1/accounts";

    @Test
    void testGetAllUserAccounts_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL);
    }

    @Test
    void testGetAllUserAccounts_authorized() {
        testAuthorizedGetWithJson(ENDPOINT_URL);
    }

    @Test
    void testGetUserAccountById_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL + "/1");
    }

    @Test
    void testGetUserAccountById_authorized() {
        testAuthorizedGetWithOptionalResource(ENDPOINT_URL + "/1");
    }

    @Test
    void testCreateUserAccount_unauthorized() {
        testUnauthorizedPost(ENDPOINT_URL, createDefaultAccountJson());
    }

    @Test
    void testCreateUserAccount_authorized() {
        // @formatter:off
        authenticatedJsonRequest()
            .body(createDefaultAccountJson())
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(201);
        // @formatter:on
    }

    @Test
    void testCreateUserAccount_forbidden() {
        // @formatter:off
        given()
            .auth().basic("user", "user")
            .contentType(ContentType.JSON)
            .body(createDefaultAccountJson())
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(403);
        // @formatter:on
    }

    @Test
    void testUpdateUserAccount_unauthorized() {
        testUnauthorizedPut(ENDPOINT_URL + "/1", createDefaultAccountJson());
    }

    @Test
    void testDeleteUserAccount_unauthorized() {
        testUnauthorizedDelete(ENDPOINT_URL + "/1");
    }
}
