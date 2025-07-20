package de.vptr.midas.api.rest.dto;

import java.util.List;

import de.vptr.midas.api.rest.entity.AccountEntity;

/**
 * Response DTO for account operations.
 * Contains computed fields and safe data for client responses.
 */
public class AccountResponseDto {

    public Long id;
    public String name;
    public int userCount;
    public List<Long> associatedUserIds;

    public AccountResponseDto(final AccountEntity entity) {
        this.id = entity.id;
        this.name = entity.name;

        // Compute user count and associated user IDs safely
        if (entity.userAccountMetas != null && !entity.userAccountMetas.isEmpty()) {
            this.userCount = entity.userAccountMetas.size();
            this.associatedUserIds = entity.userAccountMetas.stream()
                    .map(meta -> meta.user != null ? meta.user.id : null)
                    .filter(id -> id != null)
                    .toList();
        } else {
            this.userCount = 0;
            this.associatedUserIds = List.of();
        }
    }
}
