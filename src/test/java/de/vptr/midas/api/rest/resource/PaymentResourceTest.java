package de.vptr.midas.api.rest.resource;

import static de.vptr.midas.api.util.TestDataBuilder.createDefaultPaymentJson;
import static de.vptr.midas.api.util.TestDataBuilder.createUpdatedPaymentJson;
import static de.vptr.midas.api.util.TestUtil.*;

import org.junit.jupiter.api.Test;

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
    void testGetPaymentById_authorizedWithExistingPayment() {
        // TODO: expect 200 - should return existing payment data
    }

    @Test
    void testGetPaymentById_authorizedWithNonExistentPayment() {
        testAuthorizedGetWithNonExistentResource(ENDPOINT_URL + "/999");
    }

    @Test
    void testGetPaymentsByUser_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL + "/user/1");
    }

    @Test
    void testGetPaymentsByUser_authorizedWithValidUser() {
        authenticatedRequest()
                .when()
                .get(ENDPOINT_URL + "/user/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void testGetPaymentsByUser_authorizedWithDatabaseIssues() {
        // TODO: expect 500 or maybe another error with status code >500?
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
        testUnauthorizedPost(ENDPOINT_URL, createDefaultPaymentJson());
    }

    @Test
    void testCreatePayment_authorizedWithSufficientRole() {
        testAuthorizedPostWithCreation(ENDPOINT_URL, createDefaultPaymentJson());
    }

    @Test
    void testCreatePayment_authorizedWithInsufficientRole() {
        testAuthorizedPostWithInsufficientRole(ENDPOINT_URL, createDefaultPaymentJson());
    }

    @Test
    void testCreatePayment_authorizedWithValidationError() {
        // TODO: expect 400 - should return validation error for invalid data
    }

    @Test
    void testUpdatePayment_unauthorized() {
        testUnauthorizedPut(ENDPOINT_URL + "/1", createUpdatedPaymentJson());
    }

    @Test
    void testDeletePayment_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL + "/1", "DELETE");
    }
}
