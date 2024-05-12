package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.dto.CustomerDto;
import com.marinamooringmanagement.model.request.CustomerRequestDto;
import com.marinamooringmanagement.model.request.CustomerSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;

/**
 * Service interface for managing Customer entities.
 */
public interface CustomerService {
    /**
     * Saves a new customer.
     *
     * @param customerDto The DTO containing customer information.
     */
    BasicRestResponse saveCustomer(final CustomerRequestDto customerDto);

    public BasicRestResponse fetchCustomers(final CustomerSearchRequest customerSearchRequest);

    /**
     * Retrieves a customer by ID.
     *
     * @param id The ID of the customer.
     * @return The CustomerDto object.
     */
    CustomerDto getbyId(final Integer id);

    /**
     * Updates an existing customer.
     *
     * @param customerRequestDto The DTO containing updated customer information.
     * @param id                 The ID of the customer to update.
     */
    BasicRestResponse updateCustomer(final CustomerRequestDto customerRequestDto, final Integer id);

    /**
     * Deletes a customer by ID.
     *
     * @param id The ID of the customer to delete.
     */
    BasicRestResponse deleteCustomerbyId(final Integer id);

    BasicRestResponse fetchCustomerAndMooringsById(final String customerName);
}

