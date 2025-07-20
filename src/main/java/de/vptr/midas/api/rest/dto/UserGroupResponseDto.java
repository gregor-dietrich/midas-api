package de.vptr.midas.api.rest.dto;

import de.vptr.midas.api.rest.entity.UserGroupEntity;

public class UserGroupResponseDto {
    public Long id;
    public String name;
    public Long userCount;

    public UserGroupResponseDto() {
    }

    public UserGroupResponseDto(final UserGroupEntity entity) {
        this.id = entity.id;
        this.name = entity.name;
        this.userCount = entity.getUserCount();
    }
}
