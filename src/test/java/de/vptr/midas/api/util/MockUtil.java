package de.vptr.midas.api.util;

/**
 * Utility class for creating mock objects and data for testing
 */
public class MockUtil {

    private MockUtil() {
        // Utility class
    }

    /**
     * Returns common test IDs and parameters
     */
    public static class TestIds {
        public static final Long USER_ID = 1L;
        public static final Long ACCOUNT_ID = 1L;
        public static final Long POST_ID = 1L;
        public static final Long PAYMENT_ID = 1L;
        public static final Long CATEGORY_ID = 1L;
        public static final Long COMMENT_ID = 1L;
        public static final Long RANK_ID = 1L;
        public static final Long GROUP_ID = 1L;
        public static final Long PAGE_ID = 1L;
        public static final String USERNAME = "admin";
        public static final String PASSWORD = "admin";
        public static final String TEST_EMAIL = "test@example.com";

        private TestIds() {
            // Constants class
        }
    }

    /**
     * Creates a mock ID for any entity type
     */
    public static Long createMockId() {
        return TestIds.USER_ID;
    }

    /**
     * Creates a mock username for testing
     */
    public static String createMockUsername() {
        return TestIds.USERNAME;
    }

    /**
     * Creates a mock email for testing
     */
    public static String createMockEmail() {
        return TestIds.TEST_EMAIL;
    }
}
