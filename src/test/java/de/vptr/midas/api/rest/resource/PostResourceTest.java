package de.vptr.midas.api.rest.resource;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class PostResourceTest {
    private static final String ENDPOINT_URL = "/api/v1/posts";

    @Test
    void testGetAllPosts_unauthorized() {
        given()
                .when()
                .get(ENDPOINT_URL)
                .then()
                .statusCode(401);
    }
}
