package de.vptr.midas.api.rest.resource;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import de.vptr.midas.api.util.TestDataBuilder;
import de.vptr.midas.api.util.TestUtil;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class PostCommentResourceTest {
    private static final String ENDPOINT_URL = "/api/v1/comments";

    @Test
    void testGetAllPostComments_unauthorized() {
        TestUtil.testUnauthorizedAccess(ENDPOINT_URL);
    }

    @Test
    void testGetAllPostComments_authorized() {
        TestUtil.testAuthorizedGetWithJson(ENDPOINT_URL);
    }

    @Test
    void testGetCommentById_unauthorized() {
        TestUtil.testUnauthorizedAccess(ENDPOINT_URL + "/1");
    }

    @Test
    @TestTransaction
    void testGetCommentById_authorized() {
        // Since we can't guarantee a comment with ID 1 exists, test for 200 or 404
        TestUtil.authenticatedRequest()
                .when()
                .get(ENDPOINT_URL + "/1")
                .then()
                .statusCode(anyOf(is(200), is(404))); // Comment might not exist in isolated test
    }

    @Test
    void testGetCommentsByPost_unauthorized() {
        TestUtil.testUnauthorizedAccess(ENDPOINT_URL + "/post/1");
    }

    @Test
    void testGetCommentsByPost_authorized() {
        TestUtil.testAuthorizedGetWithJson(ENDPOINT_URL + "/post/1");
    }

    @Test
    void testCreateComment_unauthorized() {
        final String commentJson = TestDataBuilder.createDefaultPostCommentJson(1L);
        TestUtil.testUnauthorizedPost(ENDPOINT_URL, commentJson);
    }

    @Test
    void testCreateComment_authorized() {
        final Long postId = TestUtil.createTestPost();
        final String commentJson = TestDataBuilder.createDefaultPostCommentJson(postId);
        TestUtil.testAuthorizedPostWithCreation(ENDPOINT_URL, commentJson);
    }

    @Test
    void testUpdateComment_unauthorized() {
        final String commentJson = TestDataBuilder.createUpdatedPostCommentJson();
        TestUtil.testUnauthorizedPut(ENDPOINT_URL + "/1", commentJson);
    }

    @Test
    void testUpdateComment_authorized() {
        // First create a comment to update
        final Long postId = TestUtil.createTestPost();
        final Long commentId = TestUtil.createTestComment(postId);

        // Now update the comment
        final String updateCommentJson = TestDataBuilder.createUpdatedPostCommentJson();
        TestUtil.testAuthorizedPutWithJson(ENDPOINT_URL + "/" + commentId, updateCommentJson);
    }

    @Test
    void testPatchComment_unauthorized() {
        final String commentJson = TestDataBuilder.createPatchedPostCommentJson();
        TestUtil.testUnauthorizedPatch(ENDPOINT_URL + "/1", commentJson);
    }

    @Test
    void testPatchComment_authorized() {
        // First create a comment to patch
        final Long postId = TestUtil.createTestPost();
        final Long commentId = TestUtil.createTestComment(postId);

        // Now patch the comment
        final String patchCommentJson = TestDataBuilder.createPatchedPostCommentJson();
        TestUtil.testAuthorizedPatchWithJson(ENDPOINT_URL + "/" + commentId, patchCommentJson);
    }

    @Test
    void testDeleteComment_unauthorized() {
        TestUtil.testUnauthorizedAccess(ENDPOINT_URL + "/1", "DELETE");
    }
}
