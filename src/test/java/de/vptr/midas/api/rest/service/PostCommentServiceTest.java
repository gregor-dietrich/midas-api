package de.vptr.midas.api.rest.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.vptr.midas.api.rest.entity.*;
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
    private UserRankEntity testRank;

    @BeforeEach
    @Transactional
    void setUp() {
        // Create test rank if it doesn't exist
        this.testRank = UserRankEntity.find("name", "Test Rank").firstResult();
        if (this.testRank == null) {
            this.testRank = new UserRankEntity();
            this.testRank.name = "Test Rank";
            this.testRank.userAdd = false;
            this.testRank.userEdit = false;
            this.testRank.userDelete = false;
            this.testRank.persist();
        }

        // Create test category
        this.testCategory = new PostCategoryEntity();
        this.testCategory.name = "Test Category";
        this.testCategory = this.postCategoryService.createCategory(this.testCategory);

        // Create test user with unique username
        final String uniqueSuffix = String.valueOf(System.currentTimeMillis() + (int) (Math.random() * 10000));
        this.testUser = new UserEntity();
        this.testUser.username = "commentTestUser_" + uniqueSuffix;
        this.testUser.email = "commenttest_" + uniqueSuffix + "@example.com";
        this.testUser.password = "password";
        this.testUser = this.userService.createUser(this.testUser);

        // Create test post
        this.testPost = new PostEntity();
        this.testPost.title = "Test Post";
        this.testPost.content = "Test Content";
        this.testPost.category = this.testCategory;
        this.testPost.published = true;
        this.testPost.commentable = true;
        this.testPost = this.postService.createPost(this.testPost);
    }

    @Test
    void testServiceNotNull() {
        assertNotNull(this.postCommentService);
    }

    @Test
    void testGetAllComments() {
        final List<PostCommentEntity> comments = this.postCommentService.getAllComments();
        assertNotNull(comments);
    }

    @Test
    @Transactional
    void testCreateComment() {
        final PostCommentEntity newComment = new PostCommentEntity();
        newComment.content = "Test Comment Content";
        newComment.user = this.testUser;
        newComment.post = this.testPost;

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
        final PostCommentEntity newComment = new PostCommentEntity();
        newComment.content = "Original Comment";
        newComment.user = this.testUser;
        newComment.post = this.testPost;
        final PostCommentEntity createdComment = this.postCommentService.createComment(newComment,
                this.testUser.username);

        // Update the comment
        createdComment.content = "Updated Comment Content";

        final PostCommentEntity updatedComment = this.postCommentService.updateComment(createdComment);

        assertNotNull(updatedComment);
        assertEquals("Updated Comment Content", updatedComment.content);
    }

    @Test
    @Transactional
    void testDeleteComment() {
        // First create a comment
        final PostCommentEntity newComment = new PostCommentEntity();
        newComment.content = "Comment to delete";
        newComment.user = this.testUser;
        newComment.post = this.testPost;
        final PostCommentEntity createdComment = this.postCommentService.createComment(newComment,
                this.testUser.username);

        final Long commentId = createdComment.id;

        final boolean deleted = this.postCommentService.deleteComment(commentId);

        assertTrue(deleted);
        final Optional<PostCommentEntity> deletedComment = this.postCommentService.findById(commentId);
        assertTrue(deletedComment.isEmpty());
    }

    @Test
    void testDeleteNonExistentComment() {
        final boolean deleted = this.postCommentService.deleteComment(999999L);
        assertFalse(deleted);
    }

    @Test
    @Transactional
    void testFindById() {
        // First create a comment
        final PostCommentEntity newComment = new PostCommentEntity();
        newComment.content = "Find By ID Comment";
        newComment.user = this.testUser;
        newComment.post = this.testPost;
        final PostCommentEntity createdComment = this.postCommentService.createComment(newComment,
                this.testUser.username);

        final Optional<PostCommentEntity> foundComment = this.postCommentService.findById(createdComment.id);

        assertTrue(foundComment.isPresent());
        assertEquals("Find By ID Comment", foundComment.get().content);
    }

    @Test
    void testFindByIdNonExistent() {
        final Optional<PostCommentEntity> foundComment = this.postCommentService.findById(999999L);
        assertTrue(foundComment.isEmpty());
    }

    @Test
    @Transactional
    void testFindByPostId() {
        // First create comments
        final PostCommentEntity comment1 = new PostCommentEntity();
        comment1.content = "First comment";
        comment1.user = this.testUser;
        comment1.post = this.testPost;
        this.postCommentService.createComment(comment1, this.testUser.username);

        final PostCommentEntity comment2 = new PostCommentEntity();
        comment2.content = "Second comment";
        comment2.user = this.testUser;
        comment2.post = this.testPost;
        this.postCommentService.createComment(comment2, this.testUser.username);

        final List<PostCommentEntity> postComments = this.postCommentService.findByPostId(this.testPost.id);

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
        final PostCommentEntity comment1 = new PostCommentEntity();
        comment1.content = "User comment 1";
        comment1.user = this.testUser;
        comment1.post = this.testPost;
        this.postCommentService.createComment(comment1, this.testUser.username);

        final PostCommentEntity comment2 = new PostCommentEntity();
        comment2.content = "User comment 2";
        comment2.user = this.testUser;
        comment2.post = this.testPost;
        this.postCommentService.createComment(comment2, this.testUser.username);

        final List<PostCommentEntity> userComments = this.postCommentService.findByUserId(this.testUser.id);

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
        final PostCommentEntity comment1 = new PostCommentEntity();
        comment1.content = "Recent comment";
        comment1.user = this.testUser;
        comment1.post = this.testPost;
        this.postCommentService.createComment(comment1, this.testUser.username);

        final List<PostCommentEntity> recentComments = this.postCommentService.findRecentComments(10);

        assertNotNull(recentComments);
        assertTrue(recentComments.size() >= 1);
    }
}
