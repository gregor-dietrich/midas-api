package de.vptr.midas.api.rest.dto;

import de.vptr.midas.api.rest.entity.UserRankEntity;

public class UserRankResponseDto {

    public Long id;
    public String name;

    // Page permissions
    public Boolean pageAdd;
    public Boolean pageDelete;
    public Boolean pageEdit;

    // Post permissions
    public Boolean postAdd;
    public Boolean postDelete;
    public Boolean postEdit;

    // Post category permissions
    public Boolean postCategoryAdd;
    public Boolean postCategoryDelete;
    public Boolean postCategoryEdit;

    // Post comment permissions
    public Boolean postCommentAdd;
    public Boolean postCommentDelete;
    public Boolean postCommentEdit;

    // User permissions
    public Boolean userAdd;
    public Boolean userDelete;
    public Boolean userEdit;

    // User group permissions
    public Boolean userGroupAdd;
    public Boolean userGroupDelete;
    public Boolean userGroupEdit;

    // User account permissions
    public Boolean userAccountAdd;
    public Boolean userAccountDelete;
    public Boolean userAccountEdit;

    // User rank permissions
    public Boolean userRankAdd;
    public Boolean userRankDelete;
    public Boolean userRankEdit;

    // Computed fields
    public Long usersCount;

    public UserRankResponseDto() {
    }

    public UserRankResponseDto(final UserRankEntity entity) {
        if (entity != null) {
            this.id = entity.id;
            this.name = entity.name;

            // Page permissions
            this.pageAdd = entity.pageAdd;
            this.pageDelete = entity.pageDelete;
            this.pageEdit = entity.pageEdit;

            // Post permissions
            this.postAdd = entity.postAdd;
            this.postDelete = entity.postDelete;
            this.postEdit = entity.postEdit;

            // Post category permissions
            this.postCategoryAdd = entity.postCategoryAdd;
            this.postCategoryDelete = entity.postCategoryDelete;
            this.postCategoryEdit = entity.postCategoryEdit;

            // Post comment permissions
            this.postCommentAdd = entity.postCommentAdd;
            this.postCommentDelete = entity.postCommentDelete;
            this.postCommentEdit = entity.postCommentEdit;

            // User permissions
            this.userAdd = entity.userAdd;
            this.userDelete = entity.userDelete;
            this.userEdit = entity.userEdit;

            // User group permissions
            this.userGroupAdd = entity.userGroupAdd;
            this.userGroupDelete = entity.userGroupDelete;
            this.userGroupEdit = entity.userGroupEdit;

            // User account permissions
            this.userAccountAdd = entity.userAccountAdd;
            this.userAccountDelete = entity.userAccountDelete;
            this.userAccountEdit = entity.userAccountEdit;

            // User rank permissions
            this.userRankAdd = entity.userRankAdd;
            this.userRankDelete = entity.userRankDelete;
            this.userRankEdit = entity.userRankEdit;

            // Computed fields
            this.usersCount = entity.users != null ? (long) entity.users.size() : 0L;
        }
    }
}
