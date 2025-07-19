package de.vptr.midas.api.rest.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.vptr.midas.api.rest.entity.PostCategoryEntity;
import de.vptr.midas.api.rest.entity.PostEntity;
import de.vptr.midas.api.rest.entity.UserEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
class PostServiceTest {
    @Inject
    PostService postService;

    private UserEntity testUser;
    private PostCategoryEntity testCategory;

    @BeforeEach
    @Transactional
    void setUp() {
        // Clean up existing test data
        PostEntity.deleteAll();

        // Create test user with unique username
        final String uniqueSuffix = String.valueOf(System.currentTimeMillis() + (int) (Math.random() * 10000));
        this.testUser = new UserEntity();
        this.testUser.username = "testuser_" + uniqueSuffix;
        this.testUser.email = "test_" + uniqueSuffix + "@example.com";
        this.testUser.password = "hashedpassword";
        this.testUser.salt = "salt";
        this.testUser.activated = true;
        this.testUser.banned = false;
        this.testUser.created = LocalDateTime.now();
        this.testUser.lastLogin = LocalDateTime.now(); // Set last_login to avoid null constraint violation

        // Create or fetch a test rank
        de.vptr.midas.api.rest.entity.UserRankEntity testRank = de.vptr.midas.api.rest.entity.UserRankEntity
                .find("name", "Test Rank").firstResult();
        if (testRank == null) {
            testRank = new de.vptr.midas.api.rest.entity.UserRankEntity();
            testRank.name = "Test Rank";
            testRank.persist();
        }
        this.testUser.rank = testRank;

        this.testUser.persist();

        // Create test category if it doesn't exist
        this.testCategory = PostCategoryEntity.find("name", "Test Category").firstResult();
        if (this.testCategory == null) {
            this.testCategory = new PostCategoryEntity();
            this.testCategory.name = "Test Category";
            this.testCategory.persist();
        }
    }

    @Test
    void testServiceNotNull() {
        assertNotNull(this.postService);
    }

    @Test
    void testGetAllPosts() {
        final List<PostEntity> posts = this.postService.getAllPosts();
        assertNotNull(posts);
    }

    @Test
    void testFindPublishedPosts() {
        final List<PostEntity> publishedPosts = this.postService.findPublishedPosts();
        assertNotNull(publishedPosts);

        // All returned posts should be published
        for (final PostEntity post : publishedPosts) {
            assertTrue(post.published);
        }
    }

    @Test
    @Transactional
    void testCreatePost() {
        final PostEntity newPost = new PostEntity();
        newPost.title = "Test Post";
        newPost.content = "Test content";
        newPost.user = this.testUser;
        newPost.category = this.testCategory;

        final PostEntity createdPost = this.postService.createPost(newPost);

        assertNotNull(createdPost);
        assertNotNull(createdPost.id);
        assertEquals("Test Post", createdPost.title);
        assertEquals("Test content", createdPost.content);
        assertNotNull(createdPost.created);
        assertNotNull(createdPost.lastEdit);
        assertEquals(createdPost.created, createdPost.lastEdit);
        assertFalse(createdPost.published); // Default should be false
        assertFalse(createdPost.commentable); // Default should be false
    }

    @Test
    @Transactional
    void testCreatePostWithPublishedFlag() {
        final PostEntity newPost = new PostEntity();
        newPost.title = "Published Test Post";
        newPost.content = "Published test content";
        newPost.published = true;
        newPost.commentable = true;
        newPost.user = this.testUser;
        newPost.category = this.testCategory;

        final PostEntity createdPost = this.postService.createPost(newPost);

        assertNotNull(createdPost);
        assertTrue(createdPost.published);
        assertTrue(createdPost.commentable);
    }

    @Test
    @Transactional
    void testUpdatePost() {
        // First create a post
        final PostEntity newPost = new PostEntity();
        newPost.title = "Original Title";
        newPost.content = "Original content";
        newPost.user = this.testUser;
        newPost.category = this.testCategory;
        final PostEntity createdPost = this.postService.createPost(newPost);

        final LocalDateTime originalCreated = createdPost.created;
        final LocalDateTime originalLastEdit = createdPost.lastEdit;

        // Wait a bit to ensure timestamp difference
        try {
            Thread.sleep(10);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Update the post
        createdPost.title = "Updated Title";
        createdPost.content = "Updated content";

        final PostEntity updatedPost = this.postService.updatePost(createdPost);

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
        final PostEntity newPost = new PostEntity();
        newPost.title = "Original Title";
        newPost.content = "Original content";
        newPost.user = this.testUser;
        newPost.category = this.testCategory;
        final PostEntity createdPost = this.postService.createPost(newPost);

        // Patch only the title
        final PostEntity patchPost = new PostEntity();
        patchPost.id = createdPost.id;
        patchPost.title = "Patched Title";

        final PostEntity patchedPost = this.postService.patchPost(patchPost);

        assertNotNull(patchedPost);
        assertEquals("Patched Title", patchedPost.title);
        assertEquals("Original content", patchedPost.content); // Content should remain unchanged
    }

    @Test
    @Transactional
    void testDeletePost() {
        // First create a post
        final PostEntity newPost = new PostEntity();
        newPost.title = "To Be Deleted";
        newPost.content = "This post will be deleted";
        newPost.user = this.testUser;
        newPost.category = this.testCategory;
        final PostEntity createdPost = this.postService.createPost(newPost);

        final Long postId = createdPost.id;

        final boolean deleted = this.postService.deletePost(postId);

        assertTrue(deleted);
        final Optional<PostEntity> deletedPost = this.postService.findById(postId);
        assertTrue(deletedPost.isEmpty());
    }

    @Test
    void testDeleteNonExistentPost() {
        final boolean deleted = this.postService.deletePost(999999L);
        assertFalse(deleted);
    }

    @Test
    @Transactional
    void testFindById() {
        // First create a post
        final PostEntity newPost = new PostEntity();
        newPost.title = "Find By ID Test";
        newPost.content = "Test content";
        newPost.user = this.testUser;
        newPost.category = this.testCategory;
        final PostEntity createdPost = this.postService.createPost(newPost);

        final Optional<PostEntity> foundPost = this.postService.findById(createdPost.id);

        assertTrue(foundPost.isPresent());
        assertEquals("Find By ID Test", foundPost.get().title);
    }

    @Test
    void testFindByIdNonExistent() {
        final Optional<PostEntity> foundPost = this.postService.findById(999999L);
        assertTrue(foundPost.isEmpty());
    }

    @Test
    @Transactional
    void testFindByUserId() {
        // First create a post
        final PostEntity newPost = new PostEntity();
        newPost.title = "User Post Test";
        newPost.content = "Test content";
        newPost.user = this.testUser;
        newPost.category = this.testCategory;
        this.postService.createPost(newPost);

        final List<PostEntity> userPosts = this.postService.findByUserId(this.testUser.id);

        assertNotNull(userPosts);
        assertFalse(userPosts.isEmpty());
        assertTrue(userPosts.stream().allMatch(post -> post.user.id.equals(this.testUser.id)));
    }

    @Test
    @Transactional
    void testFindByCategoryId() {
        // First create a post
        final PostEntity newPost = new PostEntity();
        newPost.title = "Category Post Test";
        newPost.content = "Test content";
        newPost.user = this.testUser;
        newPost.category = this.testCategory;
        this.postService.createPost(newPost);

        final List<PostEntity> categoryPosts = this.postService.findByCategoryId(this.testCategory.id);

        assertNotNull(categoryPosts);
        assertFalse(categoryPosts.isEmpty());
        assertTrue(categoryPosts.stream().allMatch(post -> post.category.id.equals(this.testCategory.id)));
    }
}
