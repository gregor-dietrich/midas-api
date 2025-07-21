package de.vptr.midas.api.rest.resource;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import de.vptr.midas.api.util.TestDataBuilder;
import de.vptr.midas.api.util.TestUtil;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class PageResourceTest {
    private static final String ENDPOINT_URL = "/api/v1/pages";

    @Test
    void testGetAllPages_unauthorized() {
        TestUtil.testUnauthorizedAccess(ENDPOINT_URL);
    }

    @Test
    void testGetAllPages_authorized() {
        TestUtil.testAuthorizedGetWithJson(ENDPOINT_URL);
    }

    @Test
    void testGetPageById_unauthorized() {
        TestUtil.testUnauthorizedAccess(ENDPOINT_URL + "/1");
    }

    @Test
    void testGetPageById_authorized() {
        TestUtil.authenticatedRequest()
                .when()
                .get(ENDPOINT_URL + "/1")
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }

    @Test
    void testCreatePage_unauthorized() {
        final String pageJson = TestDataBuilder.createDefaultPageJson();
        TestUtil.testUnauthorizedPost(ENDPOINT_URL, pageJson);
    }

    @Test
    void testCreatePage_authorizedButInsufficientRole() {
        final String pageJson = TestDataBuilder.createDefaultPageJson();
        TestUtil.testAuthorizedPostWithRoleCheck(ENDPOINT_URL, pageJson); // 201 or 403
    }

    @Test
    void testUpdatePage_unauthorized() {
        final String pageJson = TestDataBuilder.createUpdatedPageJson();
        TestUtil.testUnauthorizedPut(ENDPOINT_URL + "/1", pageJson);
    }

    @Test
    void testDeletePage_unauthorized() {
        TestUtil.testUnauthorizedAccess(ENDPOINT_URL + "/1", "DELETE");
    }
}
