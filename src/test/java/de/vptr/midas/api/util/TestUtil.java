package de.vptr.midas.api.util;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

/**
 * General test utilities for REST API testing
 */
public class TestUtil {

    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "admin";

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
     * Creates a REST-assured request with basic authentication
     */
    public static RequestSpecification authenticatedRequest() {
        return given().auth().basic(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    /**
     * Creates a REST-assured request with basic authentication and JSON content
     * type
     */
    public static RequestSpecification authenticatedJsonRequest() {
        return authenticatedRequest().contentType(ContentType.JSON);
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
     * Tests authorized GET request expecting either 200 or 404 (resource may not
     * exist)
     */
    public static ValidatableResponse testAuthorizedGetWithOptionalResource(final String endpoint) {
        return authenticatedRequest()
                .when()
                .get(endpoint)
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }

    /**
     * Tests authorized GET request expecting either 200 or 404 (resource may not
     * exist) with JSON content
     */
    public static ValidatableResponse testAuthorizedGetWithOptionalResourceAndJson(final String endpoint) {
        return authenticatedRequest()
                .when()
                .get(endpoint)
                .then()
                .statusCode(anyOf(is(200), is(404)))
                .contentType(ContentType.JSON);
    }

    /**
     * Tests authorized POST request expecting either 201 or 403 (role-based access)
     */
    public static ValidatableResponse testAuthorizedPostWithRoleCheck(final String endpoint, final String jsonBody) {
        return authenticatedJsonRequest()
                .body(jsonBody)
                .when()
                .post(endpoint)
                .then()
                .statusCode(anyOf(is(201), is(403)));
    }

    /**
     * Tests authorized POST request expecting either 201, 403, or 500 (role-based
     * access with validation)
     */
    public static ValidatableResponse testAuthorizedPostWithRoleCheckAndValidation(final String endpoint,
            final String jsonBody) {
        return authenticatedJsonRequest()
                .body(jsonBody)
                .when()
                .post(endpoint)
                .then()
                .statusCode(anyOf(is(201), is(403), is(500)));
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
        final String categoryJson = TestDataBuilder.createDefaultPostCategoryJson();
        final Response response = authenticatedRequest()
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
        final Long categoryId = createTestCategory();
        final String postJson = TestDataBuilder.createPostJson("Test Post", "Test content", true, true, 1L, categoryId);
        final Response response = authenticatedRequest()
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
        final String commentJson = TestDataBuilder.createDefaultPostCommentJson(postId);
        final Response response = authenticatedRequest()
                .contentType(ContentType.JSON)
                .body(commentJson)
                .when()
                .post("/api/v1/comments");
        final Integer id = response.then().statusCode(201).extract().path("id");
        return id != null ? id.longValue() : null;
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
     * Tests a POST request with authentication that should return 201 (created)
     */
    public static ValidatableResponse testAuthorizedPostWithCreation(final String endpoint, final String requestBody) {
        return authenticatedRequest()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(endpoint)
                .then()
                .statusCode(201);
    }

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
