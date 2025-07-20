package de.vptr.midas.api.rest.dto;

import jakarta.validation.constraints.NotBlank;

public class UserGroupDto {
    @NotBlank(message = "Name is required")
    public String name;
}
