package de.vptr.midas.api.util;

/**
 * Utility class for creating mock objects and data for testing
 */
public class MockUtil {

    private MockUtil() {
        // Utility class
    }

    /**
     * Creates a mock user ID for testing
     */
    public static Long createMockUserId() {
        return 1L;
    }

    /**
     * Creates a mock account ID for testing
     */
    public static Long createMockAccountId() {
        return 1L;
    }

    /**
     * Creates a mock post ID for testing
     */
    public static Long createMockPostId() {
        return 1L;
    }

    /**
     * Creates a mock payment ID for testing
     */
    public static Long createMockPaymentId() {
        return 1L;
    }

    /**
     * Creates a mock category ID for testing
     */
    public static Long createMockCategoryId() {
        return 1L;
    }

    /**
     * Creates a mock username for testing
     */
    public static String createMockUsername() {
        return "admin";
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
        public static final String USERNAME = "admin";

        private TestIds() {
            // Constants class
        }
    }
}
