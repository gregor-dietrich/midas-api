package de.vptr.midas.api.rest.resource;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class AuthResourceTest {
  private static final String ENDPOINT_URL = "/api/v1/auth";

  @Test
  void testAuthCheckEndpoint_validAuth() {
    // @formatter:off
    given()
      .auth().basic("admin", "admin")
    .when()
      .head(ENDPOINT_URL)
    .then()
      .statusCode(200);
    // @formatter:on
  }

  @Test
  void testAuthCheckEndpoint_noAuth() {
    // @formatter:off
    given()
    .when()
      .head(ENDPOINT_URL)
    .then()
      .statusCode(401);
    // @formatter:on
  }

  @Test
  void testAuthCheckEndpoint_invalidUser() {
    // @formatter:off
    given()
      .auth().basic("root", "admin")
    .when()
      .head(ENDPOINT_URL)
    .then()
      .statusCode(401);
    // @formatter:on
  }

  @Test
  void testAuthCheckEndpoint_invalidPassword() {
    // @formatter:off
    given()
      .auth().basic("admin", "root")
    .when()
      .head(ENDPOINT_URL)
    .then()
      .statusCode(401);
    // @formatter:on
  }
}
