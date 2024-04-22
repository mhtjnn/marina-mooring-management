package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.dto.CustomerDto;
import com.marinamooringmanagement.model.request.CustomerRequestDto;
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
    BasicRestResponse saveCustomer(CustomerRequestDto customerDto);
    /**
     * Retrieves a list of customers with pagination and sorting.
     *
     * @param pageNumber The page number.
     * @param pageSize   The page size.
     * @param sortBy     The field to sort by.
     * @param sortDir    The sorting direction.
     * @return A list of CustomerDto objects.
     */
    public BasicRestResponse getCustomers(int pageNumber, int pageSize, String sortBy, String sortDir);
    /**
     * Retrieves a customer by ID.
     *
     * @param id The ID of the customer.
     * @return The CustomerDto object.
     */
    CustomerDto getbyId(Integer id);
    /**
     * Updates an existing customer.
     *
     * @param customerRequestDto The DTO containing updated customer information.
     * @param id          The ID of the customer to update.
     */
    BasicRestResponse updateCustomer(CustomerRequestDto customerRequestDto, Integer id);

    /**
     * Deletes a customer by ID.
     *
     * @param id The ID of the customer to delete.
     */
    BasicRestResponse deleteCustomerbyId(Integer id);


}
