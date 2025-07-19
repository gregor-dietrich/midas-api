package de.vptr.midas.api.rest.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.vptr.midas.api.rest.entity.PostCategoryEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class PostCategoryServiceTest {
    @Inject
    PostCategoryService postCategoryService;

    @Test
    void testServiceNotNull() {
        assertNotNull(this.postCategoryService);
    }

    @Test
    void testGetAllCategories() {
        final List<PostCategoryEntity> categories = this.postCategoryService.getAllCategories();
        assertNotNull(categories);
    }
}