package de.vptr.midas.api.rest.resource;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class UserPaymentResourceTest {
    private static final String ENDPOINT_URL = "/api/v1/user-payments";

    @Test
    void testGetAllUserPayments_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL)
        .then()
            .statusCode(401);
        // @formatter:on
    }
}
