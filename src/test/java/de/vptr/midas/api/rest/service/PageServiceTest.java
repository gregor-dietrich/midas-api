package de.vptr.midas.api.rest.service;

import static de.vptr.midas.api.util.ServiceTestUtil.assertServiceNotNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.vptr.midas.api.rest.dto.PageResponseDto;
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
        final List<PageResponseDto> pages = this.pageService.getAllPages();
        assertNotNull(pages);
    }
}