package de.vptr.midas.api.rest.service;

import static de.vptr.midas.api.util.ServiceTestDataBuilder.createUniquePostDto;
import static de.vptr.midas.api.util.ServiceTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.vptr.midas.api.rest.entity.PostCategoryEntity;
import de.vptr.midas.api.rest.entity.PostCommentEntity;
import de.vptr.midas.api.rest.entity.PostEntity;
import de.vptr.midas.api.rest.entity.UserEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
class PostCommentServiceTest {
    @Inject
    PostCommentService postCommentService;

    @Inject
    UserService userService;

    @Inject
    PostService postService;

    @Inject
    PostCategoryService postCategoryService;

    private UserEntity testUser;
    private PostEntity testPost;
    private PostCategoryEntity testCategory;

    @BeforeEach
    @Transactional
    void setUp() {
        // Create test category using utility
        this.testCategory = setupTestCategory();

        // Create test user using utility
        this.testUser = setupTestUser(this.userService);

        // Create test post using utility
        final var testPostDto = createUniquePostDto(this.testUser.id, this.testCategory.id);
        testPostDto.published = true;
        testPostDto.commentable = true;
        final var createdPost = this.postService.createPost(testPostDto);
        this.testPost = PostEntity.findById(createdPost.id);
    }

    @Test
    void testServiceNotNull() {
        assertServiceNotNull(this.postCommentService);
    }

    @Test
    void testGetAllComments() {
        final var comments = this.postCommentService.getAllComments();
        assertNotNull(comments);
    }

    @Test
    @Transactional
    void testCreateComment() {
        final PostCommentEntity newComment = de.vptr.midas.api.util.ServiceTestDataBuilder.createPostCommentEntity(
                "Test Comment Content", this.testUser, this.testPost);

        final PostCommentEntity createdComment = this.postCommentService.createComment(newComment,
                this.testUser.username);

        assertNotNull(createdComment);
        assertNotNull(createdComment.id);
        assertEquals("Test Comment Content", createdComment.content);
        assertEquals(this.testUser.id, createdComment.user.id);
        assertEquals(this.testPost.id, createdComment.post.id);
        assertNotNull(createdComment.created);
    }

    @Test
    @Transactional
    void testUpdateComment() {
        // First create a comment
        final PostCommentEntity newComment = de.vptr.midas.api.util.ServiceTestDataBuilder.createPostCommentEntity(
                "Original Comment", this.testUser, this.testPost);
        final PostCommentEntity createdComment = this.postCommentService.createComment(newComment,
                this.testUser.username);

        // Update the comment
        final PostCommentEntity updateEntity = de.vptr.midas.api.util.ServiceTestDataBuilder
                .createPostCommentUpdateEntity("Updated Comment Content");
        updateEntity.id = createdComment.id;
        updateEntity.user = createdComment.user;
        updateEntity.post = createdComment.post;

        final PostCommentEntity updatedComment = this.postCommentService.updateComment(updateEntity);

        assertNotNull(updatedComment);
        assertEquals("Updated Comment Content", updatedComment.content);
    }

    @Test
    @Transactional
    void testDeleteComment() {
        // First create a comment
        final PostCommentEntity newComment = de.vptr.midas.api.util.ServiceTestDataBuilder.createPostCommentEntity(
                "Comment to delete", this.testUser, this.testPost);
        final PostCommentEntity createdComment = this.postCommentService.createComment(newComment,
                this.testUser.username);

        final Long commentId = createdComment.id;

        final var deleted = this.postCommentService.deleteComment(commentId);

        assertTrue(deleted);
        final var deletedComment = this.postCommentService.findById(commentId);
        assertTrue(deletedComment.isEmpty());
    }

    @Test
    void testDeleteNonExistentComment() {
        final var deleted = this.postCommentService.deleteComment(999999L);
        assertFalse(deleted);
    }

    @Test
    @Transactional
    void testFindById() {
        // First create a comment
        final PostCommentEntity newComment = de.vptr.midas.api.util.ServiceTestDataBuilder.createPostCommentEntity(
                "Find By ID Comment", this.testUser, this.testPost);
        final PostCommentEntity createdComment = this.postCommentService.createComment(newComment,
                this.testUser.username);

        final var foundComment = this.postCommentService.findById(createdComment.id);

        assertTrue(foundComment.isPresent());
        assertEquals("Find By ID Comment", foundComment.get().content);
    }

    @Test
    void testFindByIdNonExistent() {
        final var foundComment = this.postCommentService.findById(999999L);
        assertTrue(foundComment.isEmpty());
    }

    @Test
    @Transactional
    void testFindByPostId() {
        // First create comments
        final PostCommentEntity comment1 = de.vptr.midas.api.util.ServiceTestDataBuilder.createPostCommentEntity(
                "First comment", this.testUser, this.testPost);
        this.postCommentService.createComment(comment1, this.testUser.username);

        final PostCommentEntity comment2 = de.vptr.midas.api.util.ServiceTestDataBuilder.createPostCommentEntity(
                "Second comment", this.testUser, this.testPost);
        this.postCommentService.createComment(comment2, this.testUser.username);

        final var postComments = this.postCommentService.findByPostId(this.testPost.id);

        assertNotNull(postComments);
        assertTrue(postComments.size() >= 2);

        // Verify all comments belong to the post
        for (final PostCommentEntity comment : postComments) {
            assertEquals(this.testPost.id, comment.post.id);
        }
    }

    @Test
    @Transactional
    void testFindByUserId() {
        // First create comments
        final PostCommentEntity comment1 = de.vptr.midas.api.util.ServiceTestDataBuilder.createPostCommentEntity(
                "User comment 1", this.testUser, this.testPost);
        this.postCommentService.createComment(comment1, this.testUser.username);

        final PostCommentEntity comment2 = de.vptr.midas.api.util.ServiceTestDataBuilder.createPostCommentEntity(
                "User comment 2", this.testUser, this.testPost);
        this.postCommentService.createComment(comment2, this.testUser.username);

        final var userComments = this.postCommentService.findByUserId(this.testUser.id);
        assertNotNull(userComments);
        assertTrue(userComments.size() >= 2);

        // Verify all comments belong to the user
        for (final PostCommentEntity comment : userComments) {
            assertEquals(this.testUser.id, comment.user.id);
        }
    }

    @Test
    @Transactional
    void testFindRecentComments() {
        // First create a comment
        final PostCommentEntity comment1 = de.vptr.midas.api.util.ServiceTestDataBuilder.createPostCommentEntity(
                "Recent comment", this.testUser, this.testPost);
        this.postCommentService.createComment(comment1, this.testUser.username);

        final var recentComments = this.postCommentService.findRecentComments(10);

        assertNotNull(recentComments);
        assertTrue(recentComments.size() >= 1);
    }
}
