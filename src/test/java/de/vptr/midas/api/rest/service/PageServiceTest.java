package de.vptr.midas.api.rest.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.vptr.midas.api.rest.entity.PageEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class PageServiceTest {
    @Inject
    PageService pageService;

    @Test
    void testServiceNotNull() {
        assertNotNull(this.pageService);
    }

    @Test
    void testGetAllPages() {
        final List<PageEntity> pages = this.pageService.getAllPages();
        assertNotNull(pages);
    }
}