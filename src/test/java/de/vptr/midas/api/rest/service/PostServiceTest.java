package de.vptr.midas.api.rest.service;

import static de.vptr.midas.api.util.ServiceTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.vptr.midas.api.rest.entity.PostCategoryEntity;
import de.vptr.midas.api.rest.entity.PostEntity;
import de.vptr.midas.api.rest.entity.UserEntity;
import de.vptr.midas.api.util.ServiceTestDataBuilder;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
class PostServiceTest {
    @Inject
    PostService postService;

    @Inject
    UserService userService;

    private UserEntity testUser;
    private PostCategoryEntity testCategory;

    @BeforeEach
    @Transactional
    void setUp() {
        // Clean up existing test data
        cleanupTestData(PostEntity.class);

        // Clean up users that might have been left from previous test runs
        this.cleanupTestUsers();

        // Create test user using utility
        this.testUser = setupTestUser(this.userService);

        // Create test category using utility
        this.testCategory = setupTestCategory();
    }

    private void cleanupTestUsers() {
        // Clean up any test users that start with "testuser_" prefix
        UserEntity.delete("username like ?1", "testuser_%");
    }

    @AfterEach
    @Transactional
    void tearDown() {
        // Clean up test data after each test
        cleanupTestData(PostEntity.class);
        this.cleanupTestUsers();
    }

    @Test
    void testServiceNotNull() {
        assertServiceNotNull(this.postService);
    }

    @Test
    void testGetAllPosts() {
        final var posts = this.postService.getAllPosts();
        assertNotNull(posts);
    }

    @Test
    void testFindPublishedPosts() {
        final var publishedPosts = this.postService.findPublishedPosts();
        assertNotNull(publishedPosts);

        // All returned posts should be published
        for (final PostEntity post : publishedPosts) {
            assertTrue(post.published);
        }
    }

    @Test
    @Transactional
    void testCreatePost() {
        final var newPost = ServiceTestDataBuilder.createUniquePostDto(this.testUser.id, this.testCategory.id);

        final var createdPost = this.postService.createPost(newPost);

        assertNotNull(createdPost);
        assertNotNull(createdPost.id);
        assertEquals(newPost.title, createdPost.title);
        assertEquals(newPost.content, createdPost.content);
        assertNotNull(createdPost.created);
        assertNotNull(createdPost.lastEdit);
        assertEquals(createdPost.created, createdPost.lastEdit);
        assertFalse(createdPost.published); // Default should be false
        assertFalse(createdPost.commentable); // Default should be false
    }

    @Test
    @Transactional
    void testCreatePostWithPublishedFlag() {
        final var newPost = de.vptr.midas.api.util.ServiceTestDataBuilder.createPostDto(
                "Published Test Post",
                "Published test content",
                true,
                true,
                this.testUser.id,
                this.testCategory.id);

        final var createdPost = this.postService.createPost(newPost);

        assertNotNull(createdPost);
        assertTrue(createdPost.published);
        assertTrue(createdPost.commentable);
    }

    @Test
    @Transactional
    void testUpdatePost() {
        // First create a post
        final var newPost = de.vptr.midas.api.util.ServiceTestDataBuilder.createPostDto(
                "Original Title",
                "Original content",
                false,
                false,
                this.testUser.id,
                this.testCategory.id);
        final var createdPost = this.postService.createPost(newPost);

        final var originalCreated = createdPost.created;
        final var originalLastEdit = createdPost.lastEdit;

        // Wait a bit to ensure timestamp difference
        try {
            Thread.sleep(10);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Update the post
        final var updateDto = de.vptr.midas.api.util.ServiceTestDataBuilder.createPostDto(
                "Updated Title",
                "Updated content",
                false,
                false,
                null,
                null);

        final var updatedPost = this.postService.updatePost(createdPost.id, updateDto);

        assertNotNull(updatedPost);
        assertEquals("Updated Title", updatedPost.title);
        assertEquals("Updated content", updatedPost.content);
        assertEquals(originalCreated, updatedPost.created);
        assertTrue(updatedPost.lastEdit.isAfter(originalLastEdit));
    }

    @Test
    @Transactional
    void testPatchPost() {
        // First create a post
        final var newPost = de.vptr.midas.api.util.ServiceTestDataBuilder.createPostDto(
                "Original Title",
                "Original content",
                false,
                false,
                this.testUser.id,
                this.testCategory.id);
        final var createdPost = this.postService.createPost(newPost);

        // Patch only the title
        final var patchDto = de.vptr.midas.api.util.ServiceTestDataBuilder.createPostDto(
                "Patched Title",
                null,
                false,
                false,
                null,
                null);

        final var patchedPost = this.postService.patchPost(createdPost.id, patchDto);

        assertNotNull(patchedPost);
        assertEquals("Patched Title", patchedPost.title);
        assertEquals("Original content", patchedPost.content); // Content should remain unchanged
    }

    @Test
    @Transactional
    void testDeletePost() {
        // First create a post
        final var newPost = de.vptr.midas.api.util.ServiceTestDataBuilder.createPostDto(
                "To Be Deleted",
                "This post will be deleted",
                false,
                false,
                this.testUser.id,
                this.testCategory.id);
        final var createdPost = this.postService.createPost(newPost);

        final var postId = createdPost.id;

        final var deleted = this.postService.deletePost(postId);

        assertTrue(deleted);
        final var deletedPost = this.postService.findById(postId);
        assertTrue(deletedPost.isEmpty());
    }

    @Test
    void testDeleteNonExistentPost() {
        final var deleted = this.postService.deletePost(999999L);
        assertFalse(deleted);
    }

    @Test
    @Transactional
    void testFindById() {
        // First create a post
        final var newPost = de.vptr.midas.api.util.ServiceTestDataBuilder.createPostDto(
                "Find By ID Test",
                "Test content",
                false,
                false,
                this.testUser.id,
                this.testCategory.id);
        final var createdPost = this.postService.createPost(newPost);

        final var foundPost = this.postService.findById(createdPost.id);

        assertTrue(foundPost.isPresent());
        assertEquals("Find By ID Test", foundPost.get().title);
    }

    @Test
    void testFindByIdNonExistent() {
        final var foundPost = this.postService.findById(999999L);
        assertTrue(foundPost.isEmpty());
    }

    @Test
    @Transactional
    void testFindByUserId() {
        // First create a post
        final var newPost = de.vptr.midas.api.util.ServiceTestDataBuilder.createPostDto(
                "User Post Test",
                "Test content",
                false,
                false,
                this.testUser.id,
                this.testCategory.id);
        this.postService.createPost(newPost);

        final var userPosts = this.postService.findByUserId(this.testUser.id);

        assertNotNull(userPosts);
        assertFalse(userPosts.isEmpty());
        assertTrue(userPosts.stream().allMatch(post -> post.user.id.equals(this.testUser.id)));
    }

    @Test
    @Transactional
    void testFindByCategoryId() {
        // First create a post
        final var newPost = de.vptr.midas.api.util.ServiceTestDataBuilder.createPostDto(
                "Category Post Test",
                "Test content",
                false,
                false,
                this.testUser.id,
                this.testCategory.id);
        this.postService.createPost(newPost);

        final var categoryPosts = this.postService.findByCategoryId(this.testCategory.id);

        assertNotNull(categoryPosts);
        assertFalse(categoryPosts.isEmpty());
        assertTrue(categoryPosts.stream().allMatch(post -> post.category.id.equals(this.testCategory.id)));
    }
}
