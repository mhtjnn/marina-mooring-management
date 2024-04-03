package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.dto.CustomerDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;

import java.util.List;
/**
 * Service interface for managing Customer entities.
 */
public interface CustomerService {
    /**
     * Saves a new customer.
     *
     * @param customerDto The DTO containing customer information.
     */
    void saveCustomer(CustomerDto customerDto);
    /**
     * Retrieves a list of customers with pagination and sorting.
     *
     * @param pageNumber The page number.
     * @param pageSize   The page size.
     * @param sortBy     The field to sort by.
     * @param sortDir    The sorting direction.
     * @return A list of CustomerDto objects.
     */
    public List<CustomerDto> getCustomers(int pageNumber, int pageSize, String sortBy, String sortDir);
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
     * @param customerDto The DTO containing updated customer information.
     * @param id          The ID of the customer to update.
     */
    BasicRestResponse updateCustomer(CustomerDto customerDto, Integer id);

    /**
     * Deletes a customer by ID.
     *
     * @param id The ID of the customer to delete.
     */
    void deletebyId(Integer id);


}
