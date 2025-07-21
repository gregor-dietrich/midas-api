package de.vptr.midas.api.util;

import de.vptr.midas.api.rest.dto.PostDto;
import de.vptr.midas.api.rest.dto.UserDto;
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
        final String suffix = ServiceTestUtil.generateUniqueTestSuffix();
        final UserDto user = new UserDto();
        user.username = "testuser_" + suffix;
        user.email = "test_" + suffix + "@example.com";
        user.password = "password123";
        return user;
    }

    /**
     * Creates a unique UserDto with custom prefix
     */
    public static UserDto createUniqueUserDto(final String usernamePrefix, final String emailPrefix) {
        final String suffix = ServiceTestUtil.generateUniqueTestSuffix();
        final UserDto user = new UserDto();
        user.username = usernamePrefix + "_" + suffix;
        user.email = emailPrefix + "_" + suffix + "@example.com";
        user.password = "password123";
        return user;
    }

    /**
     * Creates a unique PostDto for testing
     */
    public static PostDto createUniquePostDto(final Long userId, final Long categoryId) {
        final String suffix = ServiceTestUtil.generateUniqueTestSuffix();
        final PostDto post = new PostDto();
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
        final AccountEntity account = new AccountEntity();
        account.name = ServiceTestUtil.createUniqueTestName("Test Account");
        return account;
    }

    /**
     * Creates a unique PostCategoryEntity for testing
     */
    public static PostCategoryEntity createUniquePostCategoryEntity() {
        final PostCategoryEntity category = new PostCategoryEntity();
        category.name = ServiceTestUtil.createUniqueTestName("Test Category");
        return category;
    }

    /**
     * Creates a unique PageEntity for testing
     */
    public static PageEntity createUniquePageEntity() {
        final String suffix = ServiceTestUtil.generateUniqueTestSuffix();
        final PageEntity page = new PageEntity();
        page.title = "Test Page " + suffix;
        page.content = "Test content " + suffix;
        return page;
    }

    /**
     * Creates a UserDto for update operations (without password)
     */
    public static UserDto createUserUpdateDto(final String usernamePrefix, final String emailPrefix) {
        final String suffix = ServiceTestUtil.generateUniqueTestSuffix();
        final UserDto user = new UserDto();
        user.username = usernamePrefix + "_" + suffix;
        user.email = emailPrefix + "_" + suffix + "@example.com";
        user.activated = true;
        return user;
    }

    /**
     * Creates a unique UserRankDto for testing with all permissions enabled
     */
    public static de.vptr.midas.api.rest.dto.UserRankDto createUniqueUserRankDto() {
        final String suffix = ServiceTestUtil.generateUniqueTestSuffix();
        final de.vptr.midas.api.rest.dto.UserRankDto rank = new de.vptr.midas.api.rest.dto.UserRankDto();
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
        final String suffix = ServiceTestUtil.generateUniqueTestSuffix();
        final de.vptr.midas.api.rest.dto.UserGroupDto group = new de.vptr.midas.api.rest.dto.UserGroupDto();
        group.name = "Test Group " + suffix;
        return group;
    }
}
