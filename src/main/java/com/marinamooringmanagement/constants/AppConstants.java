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
        public final static String ADMINISTRATOR = "ADMINISTRATOR";

        /**
         * Represents an administrator role.
         */
        public final static String CUSTOMER_OWNER = "CUSTOMER OWNER";

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

    /**
     * This class defines constants representing different statuses.
     */
    public static class Status {

        /**
         * Indicates that an item needs inspection.
         */
        public static final String NEED_INSPECTION = "NEED_INSPECTION";

        /**
         * Indicates that the gear is off.
         */
        public static final String GEAR_OFF = "GEAR_OFF";

        /**
         * Indicates that the gear is in use.
         */
        public static final String GEAR_IN = "GEAR_IN";

        /**
         * Indicates that the item is not in use.
         */
        public static final String NOT_IN_USE = "NOT_IN_USE";
    }

    public static class WorkOrderStatusConstants {

        public static final String NEW_REQUEST = "New Request";

        public static final String WORK_IN_PROGRESS = "Work in Progress";

        public static final String PARTS_ON_ORDER = "Parts on Order";

        public static final String WAITING_ON_INSPECTION = "Waiting on Inspection";

        public static final String ON_HOLD = "On Hold";

        public static final String PENDING_APPROVAL = "Pending Approval";

        public static final String CLOSE = "Close";

    }

}
