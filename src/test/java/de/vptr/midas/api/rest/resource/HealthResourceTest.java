package de.vptr.midas.api.rest.resource;

import org.junit.jupiter.api.Test;

import de.vptr.midas.api.util.TestUtil;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class HealthResourceTest {
  private static final String ENDPOINT_URL = "/api/v1/health";

  @Test
  void testHealthCheckEndpoint() {
    TestUtil.given()
        .when()
        .head(ENDPOINT_URL)
        .then()
        .statusCode(200);
  }
}
