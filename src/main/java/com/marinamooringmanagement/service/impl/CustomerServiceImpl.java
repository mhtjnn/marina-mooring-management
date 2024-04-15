package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.DBOperationException;

import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.CustomerMapper;
import com.marinamooringmanagement.model.dto.CustomerDto;
import com.marinamooringmanagement.model.dto.MooringDto;
import com.marinamooringmanagement.model.entity.Customer;
import com.marinamooringmanagement.model.request.CustomerRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.repositories.CustomerRepository;
import com.marinamooringmanagement.service.CustomerService;
import jakarta.persistence.Basic;
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
    public BasicRestResponse saveCustomer(CustomerRequestDto customerRequestDto) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        try {
            log.info("Save the data in the database");
            Customer customer = new Customer();
            performSave(customerRequestDto, customer, null);
            response.setMessage("Customer saved successfully");
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            log.error("Exception occurred while performing save operation " + e);
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
    public BasicRestResponse getCustomers(int pageNumber, int pageSize, String sortBy, String sortDir) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        List<CustomerDto> nlst = new ArrayList<>();
        try {
            Sort sort = null;
            if (sortDir.equalsIgnoreCase("asc")) {
                sort = Sort.by(sortBy).ascending();
            } else {
                sort = Sort.by(sortBy).descending();
            }
            Pageable p = PageRequest.of(pageNumber, pageSize, sort);
            Page<Customer> pageUser = customerRepository.findAll(p);
            List<Customer> lst = pageUser.getContent();
            response.setMessage("All customers are fetched successfully");
            response.setStatus(HttpStatus.OK.value());
            MooringDto mooringDto = MooringDto.builder().build();
            mooringDto.setId(1);
            mooringDto.setCustomerName("test");
            mooringDto.setMooringNumber("test123");
            mooringDto.setHarbor("123");
            mooringDto.setWaterDepth("345");
            mooringDto.setGpsCoordinates("567");
            mooringDto.setBoatName("789");
            mooringDto.setBoatSize("91011");
            mooringDto.setBoatType("Regular");
            mooringDto.setBoatWeight("1000");
            mooringDto.setSizeOfWeight("1000");
            mooringDto.setTypeOfWeight("1000");
            mooringDto.setConditionOfEye("123");
            mooringDto.setTopChainCondition("123");
            mooringDto.setBottomChainCondition("123");
            mooringDto.setShackleSwivelCondition("123");
            mooringDto.setPennantCondition("123");
            mooringDto.setDepthAtMeanHighWater(123);


            for (Customer customer : lst) {
                CustomerDto customerDto = customerMapper.toDto(customer);
                nlst.add(customerDto);
            }

            CustomerDto customerDto = null;
            if(!nlst.isEmpty()) customerDto=nlst.get(0);
            response.setContent(List.of(nlst,customerDto,mooringDto));
        } catch (Exception e) {
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
    public CustomerDto getbyId(Integer id) {
        if (id == null) {
            throw new ResourceNotFoundException("ID cannot be null");
        }
        try {
            Optional<Customer> customerEntityOptional = customerRepository.findById(id);
            if (customerEntityOptional.isPresent()) {
                CustomerDto customerDto = customerMapper.toDto(customerEntityOptional.get());
                return customerDto;
            } else {
                throw new DBOperationException("Customer with ID : " + id + " doesn't exist");
            }
        }
        catch (Exception e){
            throw  new DBOperationException(e.getMessage(),e);
        }

    }
    /**
     * Updates a Customer entity.
     *
     * @param customerDto The CustomerDto containing the updated data.
     * @param id          The ID of the Customer to update.
     * @return A BasicRestResponse indicating the status of the operation.
     * @throws DBOperationException if the customer ID is not provided or if an error occurs during the operation.
     */
    @Override
    public BasicRestResponse updateCustomer(CustomerRequestDto customerRequestDto, Integer id) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            if (null == customerRequestDto.getId()) {
                throw new ResourceNotFoundException("Customer Id not provided for update request");
            }
            Optional<Customer> optionalCustomer = customerRepository.findById(id);

            Customer customer = optionalCustomer.get();
            performSave(customerRequestDto,customer, customerRequestDto.getId());
            response.setMessage("Customer with the given customer id updated successfully!!!");
            response.setStatus(HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error occurred while updating customer");
            throw new DBOperationException(e.getMessage(), e);
        }
        return response;
    }


    /**
     * Helper method to perform the save operation for a Customer entity.
     *
     * @param customerRequestDto The CustomerDto containing the data.
     * @param customer    The Customer entity to be updated.
     * @param id          The ID of the Customer to update.
     * @throws DBOperationException if an error occurs during the save operation.
     */
    public void performSave(CustomerRequestDto customerRequestDto, Customer customer, Integer id) {
        try {
            if (null == id) {
                customer.setLastModifiedDate(new Date(System.currentTimeMillis()));
            }
                customerMapper.mapToCustomer(customer,customerRequestDto);
                customer.setCreationDate(new Date());
                customer.setLastModifiedDate(new Date());

                customerRepository.save(customer);
        } catch (Exception e) {
            log.info("Error occurred during performSave() function");
            throw new DBOperationException(e.getMessage(), e);
        }
    }

    /**
         * Deletes a customer by ID.
         *
         * @param id The ID of the customer to delete.
         */
        @Override
        public BasicRestResponse deleteCustomerbyId (Integer id){
            BasicRestResponse response = BasicRestResponse.builder().build();
            try {
                customerRepository.deleteById(id);
                response.setMessage("customer deleted");
                response.setStatus(HttpStatus.OK.value());
                log.info("Customer Deleted Successfully");
            } catch (Exception e) {
                log.error("Error occured while deleting mooring with id");
                response.setMessage(e.getMessage());
                response.setStatus(HttpStatus.NO_CONTENT.value());

                throw new DBOperationException(e.getMessage(), e);
            }
            return response;
        }


    }
