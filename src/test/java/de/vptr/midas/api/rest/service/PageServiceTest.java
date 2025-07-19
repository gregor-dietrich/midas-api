package de.vptr.midas.api.rest.service;

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
        assertNotNull(this.pageService);
    }
}
