package com.marinamooringmanagement.security.config;

import com.marinamooringmanagement.security.model.AuthenticationDetails;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class LoggedInUserUtil {

    /**
     * Retrieves the role of the currently logged-in user.
     *
     * @return The role of the currently logged-in user.
     */
    public String getLoggedInUserRole() {
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
    public Integer getLoggedInUserID() {
        try {
            final AuthenticationDetails authDetails = (AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
            return authDetails.getLoggedInUserId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while extracting user ID of logged-in user");
        }
    }
}
