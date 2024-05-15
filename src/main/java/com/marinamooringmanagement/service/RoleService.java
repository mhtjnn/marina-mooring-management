package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.response.BasicRestResponse;

/**
 * Service interface for managing roles.
 */
public interface RoleService {

    /**
     * Fetches all roles.
     *
     * @return A {@code BasicRestResponse} containing the list of roles.
     */
    BasicRestResponse fetchRoles();
}
