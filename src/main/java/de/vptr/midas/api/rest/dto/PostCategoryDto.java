package de.vptr.midas.api.rest.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO for post category operations (POST, PUT, PATCH).
 * 
 * - POST: name required (validated by service), parentId optional
 * - PUT: name required (validated by service), parentId ignored (from URL)
 * - PATCH: name optional (allows null), parentId ignored (from URL)
 */
public class PostCategoryDto {

    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters when provided")
    public String name;

    // Required for POST operations (creation) - to set parent category
    // Ignored for PUT/PATCH operations (structure changes should be separate
    // endpoints)
    public Long parentId;

}
