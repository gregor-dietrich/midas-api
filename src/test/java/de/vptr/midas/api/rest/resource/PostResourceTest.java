package de.vptr.midas.api.rest.resource;

import static de.vptr.midas.api.util.TestDataBuilder.*;
import static de.vptr.midas.api.util.TestUtil.*;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class PostResourceTest {
    private static final String ENDPOINT_URL = "/api/v1/posts";

    @Test
    void testGetAllPosts_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL);
    }

    @Test
    void testGetAllPosts_authorized() {
        testAuthorizedGetWithJson(ENDPOINT_URL);
    }

    @Test
    void testGetPublishedPosts_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL + "/published");
    }

    @Test
    void testGetPublishedPosts_authorized() {
        testAuthorizedGetWithJson(ENDPOINT_URL + "/published");
    }

    @Test
    void testGetPost_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL + "/1");
    }

    @Test
    void testGetPost_authorizedWithExistingPost() {
        testAuthorizedGetWithExistingResource(ENDPOINT_URL + "/1");
    }

    @Test
    void testGetPost_authorizedWithNonExistentPost() {
        testAuthorizedGetWithNonExistentResource(ENDPOINT_URL + "/999");
    }

    @Test
    void testGetPostsByUser_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL + "/user/1");
    }

    @Test
    void testGetPostsByUser_authorized() {
        testAuthorizedGetWithJson(ENDPOINT_URL + "/user/1");
    }

    @Test
    void testGetPostsByCategory_unauthorized() {
        testUnauthorizedAccess(ENDPOINT_URL + "/category/1");
    }

    @Test
    void testGetPostsByCategory_authorized() {
        testAuthorizedGetWithJson(ENDPOINT_URL + "/category/1");
    }

    @Test
    void testCreatePost_unauthorized() {
        testUnauthorizedPost(ENDPOINT_URL, createDefaultPostJson());
    }

    @Test
    void testCreatePost_authorizedWithSufficientRole() {
        testAuthorizedPostWithCreation(ENDPOINT_URL, createDefaultPostJson());
    }

    @Test
    void testCreatePost_authorizedWithInsufficientRole() {
        // TODO: expect 403 - insufficient permissions to create post
    }

    @Test
    void testUpdatePost_unauthorized() {
        testUnauthorizedPut(ENDPOINT_URL + "/1", createUpdatedPostJson());
    }

    @Test
    void testPatchPost_unauthorized() {
        testUnauthorizedPatch(ENDPOINT_URL + "/1", createDefaultPostPatchJson());
    }

    @Test
    void testDeletePost_unauthorized() {
        testUnauthorizedDelete(ENDPOINT_URL + "/1");
    }
}
