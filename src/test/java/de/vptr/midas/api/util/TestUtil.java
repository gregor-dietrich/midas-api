package de.vptr.midas.api.util;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

/**
 * General test utilities for REST API testing
 */
public class TestUtil {

    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "admin";

    public static final String GUEST_USERNAME = "guest";
    public static final String GUEST_PASSWORD = "guest";

    private TestUtil() {
        // Utility class
    }

    /**
     * Provides direct access to REST-assured's given() method for custom test
     * scenarios
     */
    public static RequestSpecification given() {
        return io.restassured.RestAssured.given();
    }

    /**
     * Creates a REST-assured request with basic authentication (admin)
     */
    public static RequestSpecification authenticatedRequest() {
        return given().auth().basic(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    /**
     * Creates a REST-assured request with basic authentication and JSON content
     * type (admin)
     */
    public static RequestSpecification authenticatedJsonRequest() {
        return authenticatedRequest().contentType(ContentType.JSON);
    }

    /**
     * Creates a REST-assured request with basic authentication (guest)
     */
    public static RequestSpecification authenticatedGuestRequest() {
        return given().auth().basic(GUEST_USERNAME, GUEST_PASSWORD);
    }

    /**
     * Creates a REST-assured request with basic authentication and JSON content
     * type (guest)
     */
    public static RequestSpecification authenticatedGuestJsonRequest() {
        return authenticatedGuestRequest().contentType(ContentType.JSON);
    }

    /**
     * Tests unauthorized access to an endpoint
     */
    public static ValidatableResponse testUnauthorizedAccess(final String endpoint) {
        return given()
                .when()
                .get(endpoint)
                .then()
                .statusCode(401);
    }

    /**
     * Tests unauthorized POST access to an endpoint
     */
    public static ValidatableResponse testUnauthorizedPost(final String endpoint, final String jsonBody) {
        return testUnauthorizedAccessWithBody(endpoint, "POST", jsonBody);
    }

    /**
     * Tests unauthorized PUT access to an endpoint
     */
    public static ValidatableResponse testUnauthorizedPut(final String endpoint, final String jsonBody) {
        return testUnauthorizedAccessWithBody(endpoint, "PUT", jsonBody);
    }

    /**
     * Tests unauthorized DELETE access to an endpoint
     */
    public static ValidatableResponse testUnauthorizedDelete(final String endpoint) {
        return testUnauthorizedAccess(endpoint, "DELETE");
    }

    /**
     * Tests unauthorized access with request body for various HTTP methods
     */
    private static ValidatableResponse testUnauthorizedAccessWithBody(final String endpoint, final String httpMethod,
            final String jsonBody) {
        return switch (httpMethod.toUpperCase()) {
            case "POST" ->
                given().contentType(ContentType.JSON).body(jsonBody).when().post(endpoint).then().statusCode(401);
            case "PUT" ->
                given().contentType(ContentType.JSON).body(jsonBody).when().put(endpoint).then().statusCode(401);
            case "PATCH" ->
                given().contentType(ContentType.JSON).body(jsonBody).when().patch(endpoint).then().statusCode(401);
            default -> throw new IllegalArgumentException("Unsupported HTTP method for body requests: " + httpMethod);
        };
    }

    /**
     * Tests authorized GET request expecting 200 OK with JSON content
     */
    public static ValidatableResponse testAuthorizedGetWithJson(final String endpoint) {
        return authenticatedRequest()
                .when()
                .get(endpoint)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    /**
     * Tests authorized GET request expecting 200 OK (resource exists)
     */
    public static ValidatableResponse testAuthorizedGetWithExistingResource(final String endpoint) {
        return authenticatedRequest()
                .when()
                .get(endpoint)
                .then()
                .statusCode(200);
    }

    /**
     * Tests authorized GET request expecting 404 (resource does not exist)
     */
    public static ValidatableResponse testAuthorizedGetWithNonExistentResource(final String endpoint) {
        return authenticatedRequest()
                .when()
                .get(endpoint)
                .then()
                .statusCode(404);
    }

    /**
     * Tests authorized GET request expecting 200 OK with JSON content (resource
     * exists)
     */
    public static ValidatableResponse testAuthorizedGetWithExistingResourceAndJson(final String endpoint) {
        return authenticatedRequest()
                .when()
                .get(endpoint)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    /**
     * Tests authorized POST request expecting 201 (successful creation)
     */
    public static ValidatableResponse testAuthorizedPostWithCreation(final String endpoint, final String jsonBody) {
        return authenticatedJsonRequest()
                .body(jsonBody)
                .when()
                .post(endpoint)
                .then()
                .statusCode(201);
    }

    /**
     * Tests authorized POST request expecting 403 (insufficient role)
     */
    public static ValidatableResponse testAuthorizedPostWithInsufficientRole(final String endpoint,
            final String jsonBody) {
        return authenticatedGuestJsonRequest()
                .body(jsonBody)
                .when()
                .post(endpoint)
                .then()
                .statusCode(403);
    }

    /**
     * Tests authorized POST request expecting 400 (bad request/syntax error)
     */
    public static ValidatableResponse testAuthorizedPostWithSyntaxError(final String endpoint,
            final String jsonBody) {
        return authenticatedJsonRequest()
                .body(jsonBody)
                .when()
                .post(endpoint)
                .then()
                .statusCode(400);
    }

    /**
     * Tests authorized POST request expecting 422 (unprocessable entity/validation
     * error)
     */
    public static ValidatableResponse testAuthorizedPostWithValidationError(final String endpoint,
            final String jsonBody) {
        return authenticatedJsonRequest()
                .body(jsonBody)
                .when()
                .post(endpoint)
                .then()
                .statusCode(422);
    }

    /**
     * Tests unauthorized PATCH access to an endpoint
     */
    public static ValidatableResponse testUnauthorizedPatch(final String endpoint, final String jsonBody) {
        return testUnauthorizedAccessWithBody(endpoint, "PATCH", jsonBody);
    }

    /**
     * Generates a unique suffix based on current time and random number
     */
    public static String generateUniqueSuffix() {
        return String.valueOf(System.currentTimeMillis()) + "_" + String.valueOf((int) (Math.random() * 100000));
    }

    /**
     * Generates a unique email address for testing
     */
    public static String generateUniqueEmail(final String prefix) {
        return String.format("%s_%s@example.com", prefix, generateUniqueSuffix());
    }

    /**
     * Generates a unique username for testing
     */
    public static String generateUniqueUsername(final String prefix) {
        return String.format("%s_%s", prefix, generateUniqueSuffix());
    }

    /**
     * Tests a PUT request with authentication that should return 200 with JSON
     * response
     */
    public static ValidatableResponse testAuthorizedPutWithJson(final String endpoint, final String requestBody) {
        return authenticatedRequest()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(endpoint)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    /**
     * Creates a test category and returns its ID
     */
    public static Long createTestCategory() {
        final var categoryJson = TestDataBuilder.createDefaultPostCategoryJson();
        final var response = authenticatedRequest()
                .contentType(ContentType.JSON)
                .body(categoryJson)
                .when()
                .post("/api/v1/categories");
        final Integer id = response.then().statusCode(201).extract().path("id");
        return id != null ? id.longValue() : null;
    }

    /**
     * Creates a test post and returns its ID
     */
    public static Long createTestPost() {
        final var categoryId = createTestCategory();
        final var postJson = TestDataBuilder.createPostJson("Test Post", "Test content", true, true, 1L, categoryId);
        final var response = authenticatedRequest()
                .contentType(ContentType.JSON)
                .body(postJson)
                .when()
                .post("/api/v1/posts");
        final Integer id = response.then().statusCode(201).extract().path("id");
        return id != null ? id.longValue() : null;
    }

    /**
     * Creates a test comment and returns its ID
     */
    public static Long createTestComment(final Long postId) {
        final var commentJson = TestDataBuilder.createDefaultPostCommentJson(postId);
        final var response = authenticatedRequest()
                .contentType(ContentType.JSON)
                .body(commentJson)
                .when()
                .post("/api/v1/comments");
        final Integer id = response.then().statusCode(201).extract().path("id");
        return id != null ? id.longValue() : null;
    }

    /**
     * Creates a test account and returns its ID
     */
    public static Long createTestAccount() {
        final var accountJson = TestDataBuilder.createDefaultAccountJson();
        final var response = authenticatedRequest()
                .contentType(ContentType.JSON)
                .body(accountJson)
                .when()
                .post("/api/v1/accounts");
        final Integer id = response.then().statusCode(201).extract().path("id");
        return id != null ? id.longValue() : null;
    }

    /**
     * Creates payment JSON with test accounts
     */
    public static String createPaymentJsonWithTestAccounts() {
        final Long targetAccountId = createTestAccount();
        final Long sourceAccountId = createTestAccount();
        return TestDataBuilder.createPaymentJson(targetAccountId, sourceAccountId, 1L, "100.00", "Test payment",
                "2024-01-01");
    }

    /**
     * Tests unauthorized access with specific HTTP method
     */
    public static ValidatableResponse testUnauthorizedAccess(final String endpoint, final String httpMethod) {
        return switch (httpMethod.toUpperCase()) {
            case "GET" -> given().when().get(endpoint).then().statusCode(401);
            case "POST" -> given().when().post(endpoint).then().statusCode(401);
            case "PUT" -> given().when().put(endpoint).then().statusCode(401);
            case "PATCH" -> given().when().patch(endpoint).then().statusCode(401);
            case "DELETE" -> given().when().delete(endpoint).then().statusCode(401);
            case "HEAD" -> given().when().head(endpoint).then().statusCode(401);
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + httpMethod);
        };
    }

    /**
     * Tests a POST request with authentication that should return 201 (created) -
     * duplicate removed
     */

    /**
     * Tests a PATCH request with authentication that should return 200 with JSON
     * response
     */
    public static ValidatableResponse testAuthorizedPatchWithJson(final String endpoint, final String requestBody) {
        return authenticatedRequest()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch(endpoint)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }
}
