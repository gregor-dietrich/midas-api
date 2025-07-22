package de.vptr.midas.api.util;

import de.vptr.midas.api.rest.dto.PostDto;
import de.vptr.midas.api.rest.dto.UserDto;
import de.vptr.midas.api.rest.dto.UserGroupDto;
import de.vptr.midas.api.rest.dto.UserRankDto;
import de.vptr.midas.api.rest.entity.AccountEntity;
import de.vptr.midas.api.rest.entity.PageEntity;
import de.vptr.midas.api.rest.entity.PostCategoryEntity;

/**
 * Builder utility for creating test data objects for service tests
 */
public class ServiceTestDataBuilder {

    private ServiceTestDataBuilder() {
        // Utility class
    }

    /**
     * Creates a unique UserDto for testing
     */
    public static UserDto createUniqueUserDto() {
        final var suffix = ServiceTestUtil.generateUniqueTestSuffix();
        final var user = new UserDto();
        user.username = "testuser_" + suffix;
        user.email = "test_" + suffix + "@example.com";
        user.password = "password123";
        return user;
    }

    /**
     * Creates a unique UserDto with custom prefix
     */
    public static UserDto createUniqueUserDto(final String usernamePrefix, final String emailPrefix) {
        final var suffix = ServiceTestUtil.generateUniqueTestSuffix();
        final var user = new UserDto();
        user.username = usernamePrefix + "_" + suffix;
        user.email = emailPrefix + "_" + suffix + "@example.com";
        user.password = "password123";
        return user;
    }

    /**
     * Creates a unique PostDto for testing
     */
    public static PostDto createUniquePostDto(final Long userId, final Long categoryId) {
        final var suffix = ServiceTestUtil.generateUniqueTestSuffix();
        final var post = new PostDto();
        post.title = "Test Post " + suffix;
        post.content = "Test content " + suffix;
        post.userId = userId;
        post.categoryId = categoryId;
        post.published = false;
        post.commentable = false;
        return post;
    }

    /**
     * Creates a unique AccountEntity for testing
     */
    public static AccountEntity createUniqueAccountEntity() {
        final var account = new AccountEntity();
        account.name = ServiceTestUtil.createUniqueTestName("Test Account");
        return account;
    }

    /**
     * Creates a unique PostCategoryEntity for testing
     */
    public static PostCategoryEntity createUniquePostCategoryEntity() {
        final var category = new PostCategoryEntity();
        category.name = ServiceTestUtil.createUniqueTestName("Test Category");
        return category;
    }

    /**
     * Creates a unique PageEntity for testing
     */
    public static PageEntity createUniquePageEntity() {
        final var suffix = ServiceTestUtil.generateUniqueTestSuffix();
        final var page = new PageEntity();
        page.title = "Test Page " + suffix;
        page.content = "Test content " + suffix;
        return page;
    }

    /**
     * Creates a UserDto for update operations (without password)
     */
    public static UserDto createUserUpdateDto(final String usernamePrefix, final String emailPrefix) {
        final var suffix = ServiceTestUtil.generateUniqueTestSuffix();
        final var user = new UserDto();
        user.username = usernamePrefix + "_" + suffix;
        user.email = emailPrefix + "_" + suffix + "@example.com";
        user.activated = true;
        return user;
    }

    /**
     * Creates a unique UserRankDto for testing with all permissions enabled
     */
    public static de.vptr.midas.api.rest.dto.UserRankDto createUniqueUserRankDto() {
        final var suffix = ServiceTestUtil.generateUniqueTestSuffix();
        final var rank = new UserRankDto();
        rank.name = "Test Rank " + suffix;

        // Set all permissions to true for testing
        rank.pageAdd = true;
        rank.pageEdit = true;
        rank.pageDelete = true;
        rank.postAdd = true;
        rank.postEdit = true;
        rank.postDelete = true;
        rank.postCategoryAdd = true;
        rank.postCategoryEdit = true;
        rank.postCategoryDelete = true;
        rank.postCommentAdd = true;
        rank.postCommentEdit = true;
        rank.postCommentDelete = true;
        rank.userAdd = true;
        rank.userEdit = true;
        rank.userDelete = true;
        rank.userGroupAdd = true;
        rank.userGroupEdit = true;
        rank.userGroupDelete = true;
        rank.userRankAdd = true;
        rank.userRankEdit = true;
        rank.userRankDelete = true;
        rank.userAccountAdd = true;
        rank.userAccountEdit = true;
        rank.userAccountDelete = true;

        return rank;
    }

    /**
     * Creates a unique UserGroupDto for testing
     */
    public static de.vptr.midas.api.rest.dto.UserGroupDto createUniqueUserGroupDto() {
        final var suffix = ServiceTestUtil.generateUniqueTestSuffix();
        final var group = new UserGroupDto();
        group.name = "Test Group " + suffix;
        return group;
    }

    /**
     * Creates a UserGroupDto for update operations with a custom name
     */
    public static UserGroupDto createUserGroupUpdateDto(final String name) {
        final var group = new UserGroupDto();
        group.name = name;
        return group;
    }

    /**
     * Creates a PaymentDto for update operations
     */
    public static de.vptr.midas.api.rest.dto.PaymentDto createPaymentUpdateDto(final Long sourceAccountId,
            final Long targetAccountId, final Long userId, final String comment, final java.time.LocalDate date,
            final java.math.BigDecimal amount) {
        final var dto = new de.vptr.midas.api.rest.dto.PaymentDto();
        dto.sourceAccountId = sourceAccountId;
        dto.targetAccountId = targetAccountId;
        dto.userId = userId;
        dto.comment = comment;
        dto.date = date;
        dto.amount = amount;
        return dto;
    }

    /**
     * Creates a PostCommentEntity for update operations with custom content
     */
    public static de.vptr.midas.api.rest.entity.PostCommentEntity createPostCommentUpdateEntity(final String content) {
        final var entity = new de.vptr.midas.api.rest.entity.PostCommentEntity();
        entity.content = content;
        return entity;
    }

    /**
     * Creates a PostDto for update operations with custom title and content
     */
    public static PostDto createPostUpdateDto(final String title, final String content, final Long userId,
            final Long categoryId) {
        final var dto = new PostDto();
        dto.title = title;
        dto.content = content;
        dto.userId = userId;
        dto.categoryId = categoryId;
        return dto;
    }

    /**
     * Creates a UserRankDto for update operations with custom permissions
     */
    public static UserRankDto createUserRankUpdateDto(final String name, final boolean userAdd, final boolean userEdit,
            final boolean userDelete, final boolean postAdd, final boolean postEdit, final boolean postDelete) {
        final var dto = new UserRankDto();
        dto.name = name;
        dto.userAdd = userAdd;
        dto.userEdit = userEdit;
        dto.userDelete = userDelete;
        dto.postAdd = postAdd;
        dto.postEdit = postEdit;
        dto.postDelete = postDelete;
        return dto;
    }

    /**
     * Creates a PaymentDto with all fields set
     */
    public static de.vptr.midas.api.rest.dto.PaymentDto createPaymentDto(final Long sourceAccountId, final Long targetAccountId,
            final Long userId, final String comment, final java.time.LocalDate date, final java.math.BigDecimal amount) {
        final var dto = new de.vptr.midas.api.rest.dto.PaymentDto();
        dto.sourceAccountId = sourceAccountId;
        dto.targetAccountId = targetAccountId;
        dto.userId = userId;
        dto.comment = comment;
        dto.date = date;
        dto.amount = amount;
        return dto;
    }

    /**
     * Creates a PostCommentEntity with all fields set
     */
    public static de.vptr.midas.api.rest.entity.PostCommentEntity createPostCommentEntity(final String content,
            final de.vptr.midas.api.rest.entity.UserEntity user, final de.vptr.midas.api.rest.entity.PostEntity post) {
        final var entity = new de.vptr.midas.api.rest.entity.PostCommentEntity();
        entity.content = content;
        entity.user = user;
        entity.post = post;
        return entity;
    }

    /**
     * Creates a UserRankDto with all main permissions set
     */
    public static de.vptr.midas.api.rest.dto.UserRankDto createUserRankDto(final String name, final boolean userAdd,
            final boolean userEdit, final boolean userDelete, final boolean postAdd, final boolean postEdit, final boolean postDelete) {
        final var dto = new de.vptr.midas.api.rest.dto.UserRankDto();
        dto.name = name;
        dto.userAdd = userAdd;
        dto.userEdit = userEdit;
        dto.userDelete = userDelete;
        dto.postAdd = postAdd;
        dto.postEdit = postEdit;
        dto.postDelete = postDelete;
        return dto;
    }

    /**
     * Creates a PostDto with all fields set
     */
    public static de.vptr.midas.api.rest.dto.PostDto createPostDto(final String title, final String content, final boolean published,
            final boolean commentable, final Long userId, final Long categoryId) {
        final var dto = new de.vptr.midas.api.rest.dto.PostDto();
        dto.title = title;
        dto.content = content;
        dto.published = published;
        dto.commentable = commentable;
        dto.userId = userId;
        dto.categoryId = categoryId;
        return dto;
    }
}
