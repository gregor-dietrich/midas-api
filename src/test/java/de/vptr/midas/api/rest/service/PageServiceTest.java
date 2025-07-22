package de.vptr.midas.api.rest.service;

import static de.vptr.midas.api.util.ServiceTestUtil.assertServiceNotNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class PageServiceTest {
    @Inject
    PageService pageService;

    @Test
    void testServiceNotNull() {
        assertServiceNotNull(this.pageService);
    }

    @Test
    void testGetAllPages() {
        final var pages = this.pageService.getAllPages();
        assertNotNull(pages);
    }
}