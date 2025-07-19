package de.vptr.midas.api.rest.resource;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class UserResourceTest {
    private static final String ENDPOINT_URL = "/api/v1/users";

    @Test
    void testGetAllUsers_unauthorized() {
        given()
                .when()
                .get(ENDPOINT_URL)
                .then()
                .statusCode(401);
    }

    // Add more tests for authorized access, create, update, delete, etc. as needed
}
