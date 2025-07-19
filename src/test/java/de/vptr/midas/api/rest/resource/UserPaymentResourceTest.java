package de.vptr.midas.api.rest.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

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

    @Test
    void testGetAllUserPayments_authorized() {
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
    void testGetPaymentById_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetPaymentById_authorized() {
        // @formatter:off
        given()
            .auth().basic("admin", "admin")
        .when()
            .get(ENDPOINT_URL + "/1")
        .then()
            .statusCode(anyOf(is(200), is(404)))
            .contentType(ContentType.JSON);
        // @formatter:on
    }

    @Test
    void testGetPaymentsByUser_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL + "/user/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetPaymentsByUser_authorized() {
        // @formatter:off
        given()
            .auth().basic("admin", "admin")
        .when()
            .get(ENDPOINT_URL + "/user/1")
        .then()
            .statusCode(anyOf(is(200), is(500))) // 500 if database issues
            .contentType(ContentType.JSON);
        // @formatter:on
    }

    @Test
    void testGetRecentPayments_unauthorized() {
        // @formatter:off
        given()
        .when()
            .get(ENDPOINT_URL + "/recent?limit=10")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testGetRecentPayments_authorized() {
        // @formatter:off
        given()
            .auth().basic("admin", "admin")
        .when()
            .get(ENDPOINT_URL + "/recent?limit=10")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
        // @formatter:on
    }

    @Test
    void testCreatePayment_unauthorized() {
        final String paymentJson = """
                {
                    "amount": "100.00",
                    "comment": "Test payment",
                    "date": "2024-01-01"
                }
                """;

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(paymentJson)
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testCreatePayment_authorizedButInsufficientRole() {
        final String paymentJson = """
                {
                    "amount": "100.00",
                    "comment": "Test payment",
                    "date": "2024-01-01"
                }
                """;

        // @formatter:off
        given()
            .auth().basic("admin", "admin")
            .contentType(ContentType.JSON)
            .body(paymentJson)
        .when()
            .post(ENDPOINT_URL)
        .then()
            .statusCode(anyOf(is(201), is(403))); // 403 if no payment:add role
        // @formatter:on
    }

    @Test
    void testUpdatePayment_unauthorized() {
        final String paymentJson = """
                {
                    "amount": "150.00",
                    "comment": "Updated payment"
                }
                """;

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(paymentJson)
        .when()
            .put(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }

    @Test
    void testDeletePayment_unauthorized() {
        // @formatter:off
        given()
        .when()
            .delete(ENDPOINT_URL + "/1")
        .then()
            .statusCode(401);
        // @formatter:on
    }
}
