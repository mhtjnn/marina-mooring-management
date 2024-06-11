package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;

/**
 * Service interface for managing roles.
 */
public interface RoleService {

    /**
     * Fetches a list of roles based on the provided search request parameters.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @return a BasicRestResponse containing the results of the role search.
     */
    BasicRestResponse fetchRoles(final BaseSearchRequest baseSearchRequest);
}
