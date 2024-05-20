package com.marinamooringmanagement.constants;

/**
 * Defines authority strings for use with Spring Security's @PreAuthorize annotation.
 * <p>
 * This class provides a centralized location for defining authority strings,
 * which represent specific roles within the application's security context.
 * These strings are used to enforce access control on REST endpoints based on
 * the authenticated user's roles.
 * <p>
 * The authority strings are constructed using roles defined in {@link AppConstants.Role}.
 * They are intended to be used as expressions within the @PreAuthorize annotation
 * to specify which roles are allowed to access particular methods in REST controllers.
 * <p>
 */
public class Authority {
    /**
     * Security expression to check if the authenticated user has the authority of the "OWNER" role.
     */
    public static final String OWNER = "hasAnyAuthority('" + AppConstants.Role.ADMINISTRATOR + "')";

    /**
     * Security expression to check if the authenticated user has the authority of the "CUSTOMER_ADMIN" role.
     */
    public static final String CUSTOMER_ADMIN = "hasAnyAuthority('" + AppConstants.Role.CUSTOMER_OWNER + "')";

    /**
     * Security expression to check if the authenticated user has the authority of the "FINANCE" role.
     */
    public static final String FINANCE = "hasAnyAuthority('" + AppConstants.Role.FINANCE + "')";

    /**
     * Security expression to check if the authenticated user has the authority of the "TECHNICIAN" role.
     */
    public static final String TECHNICIAN = "hasAnyAuthority('" + AppConstants.Role.TECHNICIAN + "')";

}
