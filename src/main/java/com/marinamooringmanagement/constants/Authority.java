package com.marinamooringmanagement.constants;

/**
 * Defines authority strings for use with Spring Security's @PreAuthorize annotation.
 * <p>
 * This class provides a centralized location for defining authority strings,
 * which represent specific roles within the application's security context.
 * These strings are used to enforce access control on REST endpoints based on
 * the authenticated user's roles.
 * <p>
 * The authority strings are constructed using roles defined in {@link MetaDataConstants.Role}.
 * They are intended to be used as expressions within the @PreAuthorize annotation
 * to specify which roles are allowed to access particular methods in REST controllers.
 * <p>
 */
public class Authority {
    public static final String USER = "hasAnyAuthority('" + MetaDataConstants.Role.USER + "')";
    public static final String ADMINISTRATOR = "hasAnyAuthority('" + MetaDataConstants.Role.ADMINISTRATOR + "')";
}
