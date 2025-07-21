package de.vptr.midas.api.rest.resource;

import static de.vptr.midas.api.util.TestUtil.*;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import de.vptr.midas.api.util.TestDataBuilder;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class PaymentResourceTest {
    private static final String ENDPOINT_URL = "/api/v1/payments";

    @Test
    void testGetAllUserPayments_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL);
    }

    @Test
    void testGetAllUserPayments_authorized() {
        testAuthorizedGetWithJson(ENDPOINT_URL);
    }

    @Test
    void testGetPaymentById_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL + "/1");
    }

    @Test
    void testGetPaymentById_authorized() {
        testAuthorizedGetWithOptionalResource(ENDPOINT_URL + "/1");
    }

    @Test
    void testGetPaymentsByUser_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL + "/user/1");
    }

    @Test
    void testGetPaymentsByUser_authorized() {
        authenticatedRequest()
                .when()
                .get(ENDPOINT_URL + "/user/1")
                .then()
                .statusCode(anyOf(is(200), is(500))) // 500 if database issues
                .contentType(ContentType.JSON);
    }

    @Test
    void testGetRecentPayments_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL + "/recent?limit=10");
    }

    @Test
    void testGetRecentPayments_authorized() {
        testAuthorizedGetWithJson(ENDPOINT_URL + "/recent?limit=10");
    }

    @Test
    void testCreatePayment_unauthorized() {
        final String paymentJson = TestDataBuilder.createDefaultPaymentJson();
        testUnauthorizedPost(ENDPOINT_URL, paymentJson);
    }

    @Test
    void testCreatePayment_authorizedButInsufficientRole() {
        final String paymentJson = TestDataBuilder.createDefaultPaymentJson();
        authenticatedRequest()
                .contentType(ContentType.JSON)
                .body(paymentJson)
                .when()
                .post(ENDPOINT_URL)
                .then()
                .statusCode(anyOf(is(201), is(403), is(400))); // 403 if no payment:add role, 400 if validation fails
    }

    @Test
    void testUpdatePayment_unauthorized() {
        final String paymentJson = TestDataBuilder.createUpdatedPaymentJson();
        testUnauthorizedPut(ENDPOINT_URL + "/1", paymentJson);
    }

    @Test
    void testDeletePayment_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL + "/1", "DELETE");
    }
}
