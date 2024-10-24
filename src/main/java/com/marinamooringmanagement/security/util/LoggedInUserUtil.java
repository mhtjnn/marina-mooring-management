package com.marinamooringmanagement.security.util;

import com.marinamooringmanagement.security.model.AuthenticationDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LoggedInUserUtil {

    /**
     * Retrieves the role of the currently logged-in user.
     *
     * @return The role of the currently logged-in user.
     */
    public static String getLoggedInUserRole() {
        try {
            final AuthenticationDetails authDetails = (AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
            return authDetails.getLoggedInUserRole();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while extracting role of logged-in user");
        }
    }

    /**
     * Retrieves the ID of the currently logged-in user.
     *
     * @return The ID of the currently logged-in user.
     */
    public static Integer getLoggedInUserID() {
        try {
            final AuthenticationDetails authDetails = (AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
            return authDetails.getLoggedInUserId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while extracting user ID of logged-in user");
        }
    }

    public static String getLoggedInUserEmail() {
        try {
            final AuthenticationDetails authDetails = (AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
            return authDetails.getLoggedInUserEmail();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while extracting user ID of logged-in user");
        }
    }
}
