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

        public static final String COMPLETED = "Completed";

        public static final String DENIED = "Denied";

    }

    public static class CustomerTypeConstants {
        public static final String NEW_INSTALL = "New Install";

        public static final String RETAIL = "Retail";

        public static final String INTERNET = "Internet";

        public static final String FORMER = "Former";

        public static final String DOCK = "Dock";
    }

    public static class MooringDueServiceStatusConstants {

        public static final String COMPLETE = "Complete";

        public static final String PENDING = "Pending";
    }

    public static class BooleanStringConst {

        public static final String YES = "Yes";

        public static final String NO = "No";
    }

    public static class WorkOrderPayStatusConstants {

        public static final String APPROVED = "Approved";

        public static final String DENIED = "Denied";

        public static final String NOACTION = "No action";
    }

    public static class WorkOrderInvoiceStatusConstants {

        public static final String PAID = "Paid";

        public static final String PENDING = "Pending";

        public static final String EXCEEDED = "Exceeding";
    }

    public static class EntityConstants {

        public static final String CUSTOMER = "Customer";

        public static final String WORK_ORDER = "WorkOrder";

        public static final String MOORING = "Mooring";

        public static final String USER = "User";
    }

    public static class ServiceAreaTypeConstants {

        public static final String DOCKS = "Docks";

        public static final String MARINA = "Marinas";

        public static final String HARBORS = "Harbors";

        public static final String MOORING_FIELDS = "Mooring Fields";

        public static final String PIERS = "Piers";

        public static final String ANCHORAGES = "Anchorages";

        public static final String BOAT_CLUBS = "Boat Clubs";

        public static final String PORTS = "Ports";

        public static final String PRIVATE_MOORINGS = "Private Moorings";

    }

    public static class HeaderConstants {

        public static final String CUSTOMER_OWNER_ID = "CUSTOMER_OWNER_ID";

    }
}
