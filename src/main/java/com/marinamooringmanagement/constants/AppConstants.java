package com.marinamooringmanagement.constants;
/**
 * Class to define Constants like Role.
 */
public class AppConstants {

    /**
     * Constants related to user roles.
     */
    public static class Role {
        /**
         * Represents a regular user role.
         */
        public final static String OWNER = "OWNER";

        /**
         * Represents an administrator role.
         */
        public final static String CUSTOMER_ADMIN = "CUSTOMER_ADMIN";

        /**
         * Represents a finance user role.
         */
        public final static String FINANCE = "FINANCE";

        /**
         * Represents a technician user role.
         */
        public final static String TECHNICIAN = "TECHNICIAN";
    }

    /**
     * Constants related to default page settings.
     */
    public static class DefaultPageConst {
        /**
         * Default page size for pagination.
         */
        public static final String DEFAULT_PAGE_SIZE = "20";

        /**
         * Default page number for pagination.
         */
        public static final String DEFAULT_PAGE_NUM = "0";
    }
}
