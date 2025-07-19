package de.vptr.midas.api.rest.dto;

import de.vptr.midas.api.rest.entity.AccountEntity;

public class AccountDto {
    public Long id;
    public String name;

    public static AccountDto fromEntity(final AccountEntity entity) {
        final var dto = new AccountDto();
        dto.id = entity.id;
        dto.name = entity.name;
        return dto;
    }
}
