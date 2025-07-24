package de.vptr.midas.api.rest.resource;

import static de.vptr.midas.api.util.TestDataBuilder.*;

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
    void testGetPageById_authorizedWithExistingPage() {
        TestUtil.testAuthorizedGetWithExistingResource(ENDPOINT_URL + "/1");
    }

    @Test
    void testGetPageById_authorizedWithNonExistentPage() {
        TestUtil.testAuthorizedGetWithNonExistentResource(ENDPOINT_URL + "/999");
    }

    @Test
    void testCreatePage_unauthorized() {
        final String pageJson = TestDataBuilder.createDefaultPageJson();
        TestUtil.testUnauthorizedPost(ENDPOINT_URL, pageJson);
    }

    @Test
    void testCreatePage_authorizedWithSufficientRole() {
        final String pageJson = TestDataBuilder.createDefaultPageJson();
        TestUtil.testAuthorizedPostWithCreation(ENDPOINT_URL, pageJson);
    }

    @Test
    void testCreatePage_authorizedWithInsufficientRole() {
        final String pageJson = TestDataBuilder.createDefaultPageJson();
        TestUtil.testAuthorizedPostWithInsufficientRole(ENDPOINT_URL, pageJson);
    }

    @Test
    void testCreatePage_authorizedWithEmptyJson() {
        TestUtil.testAuthorizedPostWithValidationError(ENDPOINT_URL, createEmptyJson());
    }

    @Test
    void testCreatePage_authorizedWithInvalidJson() {
        TestUtil.testAuthorizedPostWithValidationError(ENDPOINT_URL, createInvalidJson());
    }

    @Test
    void testCreatePage_authorizedWithMalformedJson() {
        TestUtil.testAuthorizedPostWithSyntaxError(ENDPOINT_URL, createMalformedJson());
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
