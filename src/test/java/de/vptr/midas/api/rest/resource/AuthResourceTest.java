package de.vptr.midas.api.rest.resource;

import org.junit.jupiter.api.Test;

import de.vptr.midas.api.util.TestUtil;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class AuthResourceTest {
  private static final String ENDPOINT_URL = "/api/v1/auth";

  @Test
  void testAuthCheckEndpoint_validAuth() {
    TestUtil.authenticatedRequest()
        .when()
        .head(ENDPOINT_URL)
        .then()
        .statusCode(200);
  }

  @Test
  void testAuthCheckEndpoint_noAuth() {
    TestUtil.given()
        .when()
        .head(ENDPOINT_URL)
        .then()
        .statusCode(401);
  }

  @Test
  void testAuthCheckEndpoint_invalidUser() {
    TestUtil.given()
        .auth().basic("root", "admin")
        .when()
        .head(ENDPOINT_URL)
        .then()
        .statusCode(401);
  }

  @Test
  void testAuthCheckEndpoint_invalidPassword() {
    TestUtil.given()
        .auth().basic("admin", "root")
        .when()
        .head(ENDPOINT_URL)
        .then()
        .statusCode(401);
  }
}
