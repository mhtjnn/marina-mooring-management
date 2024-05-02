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

        public final static String FINANCE = "FINANCE";

        public final static String TECHNICIAN = "TECHNICIAN";
    }

    public static class DefaultPageConst {
        public static final String DEFAULT_PAGE_SIZE = "20";
        public static final String DEFAULT_PAGE_NUM = "0";
    }
}

