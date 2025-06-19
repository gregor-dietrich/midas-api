package de.vptr.midas.api.rest.resource;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class HealthResourceTest {
  private static final String ENDPOINT_URL = "/api/v1/health";

  @Test
  void testHealthCheckEndpoint() {
    // @formatter:off
    given()
    .when()
      .head(ENDPOINT_URL)
    .then()
      .statusCode(200);
    // @formatter:on
  }
}
