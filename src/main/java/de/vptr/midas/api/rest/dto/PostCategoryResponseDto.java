package de.vptr.midas.api.rest.dto;

import java.util.List;

import de.vptr.midas.api.rest.entity.PostCategoryEntity;

/**
 * Response DTO for post category operations.
 * Contains computed fields and safe data for client responses.
 */
public class PostCategoryResponseDto {

    public Long id;
    public String name;
    public Long parentId;
    public String parentName;
    public boolean isRootCategory;
    public int childrenCount;
    public int postsCount;
    public List<Long> childrenIds;

    public PostCategoryResponseDto(final PostCategoryEntity entity) {
        this.id = entity.id;
        this.name = entity.name;
        this.isRootCategory = entity.isRootCategory();

        // Handle parent information safely
        if (entity.parent != null) {
            this.parentId = entity.parent.id;
            this.parentName = entity.parent.name;
        }

        // Compute children count and IDs safely
        if (entity.children != null && !entity.children.isEmpty()) {
            this.childrenCount = entity.children.size();
            this.childrenIds = entity.children.stream()
                    .map(child -> child.id)
                    .toList();
        } else {
            this.childrenCount = 0;
            this.childrenIds = List.of();
        }

        // Compute posts count safely
        if (entity.posts != null) {
            this.postsCount = entity.posts.size();
        } else {
            this.postsCount = 0;
        }
    }
}
