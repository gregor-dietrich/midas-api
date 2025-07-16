package de.vptr.midas.api.rest.dto;

import de.vptr.midas.api.rest.entity.UserAccountEntity;

public class UserAccountDto {
    public Long id;
    public String name;

    public static UserAccountDto fromEntity(final UserAccountEntity entity) {
        final var dto = new UserAccountDto();
        dto.id = entity.id;
        dto.name = entity.name;
        return dto;
    }
}
