package de.vptr.midas.api.rest.service;

import static de.vptr.midas.api.util.ServiceTestDataBuilder.createUniquePostDto;
import static de.vptr.midas.api.util.ServiceTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.vptr.midas.api.rest.dto.PostDto;
import de.vptr.midas.api.rest.dto.PostResponseDto;
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

    @Inject
    UserService userService;

    private UserEntity testUser;
    private PostCategoryEntity testCategory;

    @BeforeEach
    @Transactional
    void setUp() {
        // Clean up existing test data
        cleanupTestData(PostEntity.class);

        // Create test user using utility
        this.testUser = setupTestUser(this.userService);

        // Create test category using utility
        this.testCategory = setupTestCategory();
    }

    @Test
    void testServiceNotNull() {
        assertServiceNotNull(this.postService);
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
        final PostDto newPost = createUniquePostDto(this.testUser.id, this.testCategory.id);

        final PostResponseDto createdPost = this.postService.createPost(newPost);

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
        final PostDto newPost = new PostDto();
        newPost.title = "Published Test Post";
        newPost.content = "Published test content";
        newPost.published = true;
        newPost.commentable = true;
        newPost.userId = this.testUser.id;
        newPost.categoryId = this.testCategory.id;

        final PostResponseDto createdPost = this.postService.createPost(newPost);

        assertNotNull(createdPost);
        assertTrue(createdPost.published);
        assertTrue(createdPost.commentable);
    }

    @Test
    @Transactional
    void testUpdatePost() {
        // First create a post
        final PostDto newPost = new PostDto();
        newPost.title = "Original Title";
        newPost.content = "Original content";
        newPost.userId = this.testUser.id;
        newPost.categoryId = this.testCategory.id;
        final PostResponseDto createdPost = this.postService.createPost(newPost);

        final LocalDateTime originalCreated = createdPost.created;
        final LocalDateTime originalLastEdit = createdPost.lastEdit;

        // Wait a bit to ensure timestamp difference
        try {
            Thread.sleep(10);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Update the post
        final PostDto updateDto = new PostDto();
        updateDto.title = "Updated Title";
        updateDto.content = "Updated content";

        final PostResponseDto updatedPost = this.postService.updatePost(createdPost.id, updateDto);

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
        final PostDto newPost = new PostDto();
        newPost.title = "Original Title";
        newPost.content = "Original content";
        newPost.userId = this.testUser.id;
        newPost.categoryId = this.testCategory.id;
        final PostResponseDto createdPost = this.postService.createPost(newPost);

        // Patch only the title
        final PostDto patchDto = new PostDto();
        patchDto.title = "Patched Title";

        final PostResponseDto patchedPost = this.postService.patchPost(createdPost.id, patchDto);

        assertNotNull(patchedPost);
        assertEquals("Patched Title", patchedPost.title);
        assertEquals("Original content", patchedPost.content); // Content should remain unchanged
    }

    @Test
    @Transactional
    void testDeletePost() {
        // First create a post
        final PostDto newPost = new PostDto();
        newPost.title = "To Be Deleted";
        newPost.content = "This post will be deleted";
        newPost.userId = this.testUser.id;
        newPost.categoryId = this.testCategory.id;
        final PostResponseDto createdPost = this.postService.createPost(newPost);

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
        final PostDto newPost = new PostDto();
        newPost.title = "Find By ID Test";
        newPost.content = "Test content";
        newPost.userId = this.testUser.id;
        newPost.categoryId = this.testCategory.id;
        final PostResponseDto createdPost = this.postService.createPost(newPost);

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
        final PostDto newPost = new PostDto();
        newPost.title = "User Post Test";
        newPost.content = "Test content";
        newPost.userId = this.testUser.id;
        newPost.categoryId = this.testCategory.id;
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
        final PostDto newPost = new PostDto();
        newPost.title = "Category Post Test";
        newPost.content = "Test content";
        newPost.userId = this.testUser.id;
        newPost.categoryId = this.testCategory.id;
        this.postService.createPost(newPost);

        final List<PostEntity> categoryPosts = this.postService.findByCategoryId(this.testCategory.id);

        assertNotNull(categoryPosts);
        assertFalse(categoryPosts.isEmpty());
        assertTrue(categoryPosts.stream().allMatch(post -> post.category.id.equals(this.testCategory.id)));
    }
}
