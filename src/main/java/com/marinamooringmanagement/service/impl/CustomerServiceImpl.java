package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.DBOperationException;

import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.CustomerMapper;
import com.marinamooringmanagement.model.dto.CustomerDto;
import com.marinamooringmanagement.model.entity.Customer;
import com.marinamooringmanagement.model.request.CustomerRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.repositories.CustomerRepository;
import com.marinamooringmanagement.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the CustomerService interface.
 */
@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    /**
     * Saves a new customer.
     *
     * @param customerRequestDto The DTO containing customer information.
     */
    @Override
    public BasicRestResponse saveCustomer(final CustomerRequestDto customerRequestDto) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        try {
            log.info(String.format("Saving data in the database for Customer ID %d", customerRequestDto.getId()));

            final Customer customer = new Customer();
            performSave(customerRequestDto, customer, null);
            response.setMessage("Customer saved successfully");
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            log.error(String.format("Exception occurred while performing save operation: %s", e.getMessage()), e);

            throw new DBOperationException(e.getMessage(), e);
        }

        return response;
    }


    /**
     * Retrieves a list of customers with pagination and sorting.
     *
     * @param pageNumber The page number.
     * @param pageSize   The page size.
     * @param sortBy     The field to sort by.
     * @param sortDir    The sorting direction.
     * @return A list of CustomerDto objects.
     */
    public BasicRestResponse getCustomers(final int pageNumber, final int pageSize, final String sortBy, final String sortDir) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        List<CustomerDto> customerDtoList;
        try {
            final Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

            final Pageable p = PageRequest.of(pageNumber, pageSize, sort);
            final Page<Customer> pageUser = customerRepository.findAll(p);
            List<Customer> customerList = pageUser.getContent();
            response.setMessage("All customers are fetched successfully");
            response.setStatus(HttpStatus.OK.value());

            customerDtoList = customerList.stream()
                    .map(customerMapper::toDto)
                    .collect(Collectors.toList());


            CustomerDto customerDto = !customerDtoList.isEmpty() ? customerDtoList.get(0) : null;
            response.setContent(customerDtoList);
            log.info(String.format("Customers fetched successfully"));

        } catch (Exception e) {
            log.error(String.format("Error occurred while fetching Customers: %s", e.getMessage()), e);

            throw new DBOperationException(e.getMessage(), e);
        }
        return response;
    }


    /**
     * Retrieves a customer by ID.
     *
     * @param id The ID of the customer.
     * @return The CustomerDto object.
     */
    @Override
    public CustomerDto getbyId(final Integer id) {
        if (id == null) {
            throw new ResourceNotFoundException("ID cannot be null");
        }
        try {
            Optional<Customer> customerEntityOptional = customerRepository.findById(id);
            if (customerEntityOptional.isPresent()) {
                log.info(String.format("Successfully retrieved Customer data for ID: %d", id));

                return customerMapper.toDto(customerEntityOptional.get());


            } else {
                throw new DBOperationException(String.format("Customer with ID : %d doesn't exist", id));
            }
        } catch (Exception e) {
            log.error(String.format("Error occurred while retrieving Customer for ID: %d: %s", id, e.getMessage()), e);

            throw new DBOperationException(e.getMessage(), e);
        }

    }

    /**
     * Updates a Customer entity.
     *
     * @param customerRequestDto The CustomerDto containing the updated data.
     * @param id                 The ID of the Customer to update.
     * @return A BasicRestResponse indicating the status of the operation.
     * @throws DBOperationException if the customer ID is not provided or if an error occurs during the operation.
     */
    @Override
    public BasicRestResponse updateCustomer(final CustomerRequestDto customerRequestDto, final Integer id) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            if (null == customerRequestDto.getId()) {
                log.info(String.format("Update attempt without a Customer ID provided in the request DTO"));

                throw new ResourceNotFoundException(String.format("Customer not found with id: %1$s", id));
            }
            Optional<Customer> optionalCustomer = customerRepository.findById(id);
            if (optionalCustomer.isPresent()) {

                Customer customer = optionalCustomer.get();
                performSave(customerRequestDto, customer, customerRequestDto.getId());
                response.setMessage("Customer with the given customer id updated successfully!!!");
                response.setStatus(HttpStatus.OK.value());

            }
        } catch (Exception e) {
            log.error(String.format("Error occurred while updating customer: %s", e.getMessage()), e);

            throw new DBOperationException(e.getMessage(), e);
        }
        return response;
    }


    /**
     * Helper method to perform the save operation for a Customer entity.
     *
     * @param customerRequestDto The CustomerDto containing the data.
     * @param customer           The Customer entity to be updated.
     * @param id                 The ID of the Customer to update.
     * @throws DBOperationException if an error occurs during the save operation.
     */
    public void performSave(final CustomerRequestDto customerRequestDto, final Customer customer, final Integer id) {
        try {
            if (null == id) {
                customer.setLastModifiedDate(new Date(System.currentTimeMillis()));
            }
            customerMapper.mapToCustomer(customer, customerRequestDto);
            customer.setCreationDate(new Date());
            customer.setLastModifiedDate(new Date());

            customerRepository.save(customer);
            log.info(String.format("Customer saved successfully with ID: %d", customer.getId()));

        } catch (Exception e) {
            log.error(String.format("Error occurred during performSave() function: %s", e.getMessage()), e);

            throw new DBOperationException(e.getMessage(), e);
        }
    }

    /**
     * Deletes a customer by ID.
     *
     * @param id The ID of the customer to delete.
     */
    @Override
    public BasicRestResponse deleteCustomerbyId(final Integer id) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        try {
            customerRepository.deleteById(id);
            response.setMessage(String.format("Customer with ID %d deleted successfully", id));
            response.setStatus(HttpStatus.OK.value());
            log.info(String.format("Customer with ID %d deleted successfully", id));

        } catch (Exception e) {
            log.error(String.format("Error occurred while deleting customer with ID %d", id));

            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.NO_CONTENT.value());

            throw new DBOperationException(e.getMessage(), e);
        }
        return response;
    }


}
