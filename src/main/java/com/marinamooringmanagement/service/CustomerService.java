package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.dto.CustomerDto;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.CustomerRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Service interface for managing Customer entities.
 */
public interface CustomerService {
    /**
     * Saves a new customer.
     *
     * @param customerDto The DTO containing customer information.
     */
    BasicRestResponse saveCustomer(final CustomerRequestDto customerDto, final HttpServletRequest request);

    /**
     * Fetches a list of customers based on the provided search request parameters and search text.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @param searchText the text used to search for specific customers by name, email, or other relevant criteria.
     * @return a BasicRestResponse containing the results of the customer search.
     */
    BasicRestResponse fetchCustomers(final BaseSearchRequest baseSearchRequest, final String searchText, final HttpServletRequest request);

    /**
     * Retrieves a customer by ID.
     *
     * @param id The ID of the customer.
     * @return The CustomerDto object.
     */
    CustomerDto getById(final Integer id);

    /**
     * Updates an existing customer.
     *
     * @param customerRequestDto The DTO containing updated customer information.
     * @param id                 The ID of the customer to update.
     */
    BasicRestResponse updateCustomer(final CustomerRequestDto customerRequestDto, final Integer id, final HttpServletRequest request);

    /**
     * Deletes a customer by ID.
     *
     * @param id The ID of the customer to delete.
     */
    BasicRestResponse deleteCustomerById(final Integer id, final HttpServletRequest request);

    /**
     * Fetches the details of a customer and their associated moorings based on the provided customer ID.
     *
     * @param customerId the unique identifier of the customer whose details and moorings are to be fetched.
     * @return a BasicRestResponse containing the customer details and their associated moorings.
     */
    BasicRestResponse fetchCustomerAndMooringsWithCustomerImages(final BaseSearchRequest baseSearchRequest, final Integer customerId, final HttpServletRequest request);

    BasicRestResponse fetchCustomerAndMooringsWithMooringImages(final BaseSearchRequest baseSearchRequest, final Integer customerId, final HttpServletRequest request);
}

