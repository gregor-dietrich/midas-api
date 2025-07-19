package de.vptr.midas.api.rest.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class UserServiceTest {
    @Inject
    UserService userService;

    @Test
    void testServiceNotNull() {
        assertNotNull(this.userService);
    }
}
