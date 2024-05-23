package com.marinamooringmanagement.security.config;

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
    public String getLoggedInUserRole() {
        final AuthenticationDetails authDetails = (AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return authDetails.getLoggedInUserRole();
    }

    /**
     * Retrieves the ID of the currently logged-in user.
     *
     * @return The ID of the currently logged-in user.
     */
    public Integer getLoggedInUserID() {
        final AuthenticationDetails authDetails = (AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return authDetails.getLoggedInUserId();
    }


}
