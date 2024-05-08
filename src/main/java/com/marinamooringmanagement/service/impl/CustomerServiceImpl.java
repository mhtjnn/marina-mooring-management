package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.CustomerMapper;
import com.marinamooringmanagement.mapper.MooringMapper;
import com.marinamooringmanagement.model.dto.CustomerDto;
import com.marinamooringmanagement.model.entity.Customer;
import com.marinamooringmanagement.model.entity.Mooring;
import com.marinamooringmanagement.model.request.CustomerRequestDto;
import com.marinamooringmanagement.model.request.CustomerSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.CustomerAndMooringsCustomResponse;
import com.marinamooringmanagement.model.response.CustomerResponseDto;
import com.marinamooringmanagement.model.response.MooringResponseDto;
import com.marinamooringmanagement.repositories.CustomerRepository;
import com.marinamooringmanagement.repositories.MooringRepository;
import com.marinamooringmanagement.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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

    @Autowired
    private MooringMapper mooringMapper;

    @Autowired
    private MooringRepository mooringRepository;

    @Autowired
    private MooringServiceImpl mooringService;

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

            final Customer customer = Customer.builder().build();

            performSave(customerRequestDto, customer, null);

            response.setMessage("Customer saved successfully");
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            log.error(String.format("Exception occurred while performing save operation: %s", e.getMessage()), e);

            throw new DBOperationException(e.getMessage(), e);
        }

        return response;
    }

    public BasicRestResponse fetchCustomers(final CustomerSearchRequest customerSearchRequest) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Pageable p = PageRequest.of(customerSearchRequest.getPageNumber(), customerSearchRequest.getPageSize(), customerSearchRequest.getSort());

            final Page<Customer> pageUser = customerRepository.findAll(p);

            List<Customer> customerList = pageUser.getContent();

            if(customerList.isEmpty()) throw new DBOperationException("No customers found in the database");

            final List<CustomerResponseDto> customerResponseDtoList = customerList.stream().map(customer -> customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), customer)).toList();

            response.setContent(customerResponseDtoList);
            response.setMessage("All customers are fetched successfully");
            response.setStatus(HttpStatus.OK.value());

            log.info(String.format("Customers fetched successfully"));

        } catch (Exception e) {
            log.error(String.format("Error occurred while fetching Customers: %s", e.getMessage()), e);
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }

    @Override
    public BasicRestResponse fetchCustomerAndMooringsById(final Integer customerId) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            CustomerAndMooringsCustomResponse customerAndMooringsCustomResponse = CustomerAndMooringsCustomResponse.builder().build();
            Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

            if(optionalCustomer.isEmpty()) throw new ResourceNotFoundException("No customer found with the given ID");

            CustomerResponseDto customerResponseDto = CustomerResponseDto.builder().build();
            customerMapper.mapToCustomerResponseDto(customerResponseDto, optionalCustomer.get());

            List<Mooring> mooringList = optionalCustomer.get().getMooringList();
            List<String> boatyardNames = new ArrayList<>();
            List<MooringResponseDto> mooringResponseDtoList = new ArrayList<>();

            if(!mooringList.isEmpty()) {
                boatyardNames = mooringList.stream().map(Mooring::getBoatyardName).toList();
                mooringResponseDtoList = mooringList.stream().map(mooring -> mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), mooring)).toList();
            }

            customerResponseDto.setMooringResponseDtoList(mooringResponseDtoList);
            customerAndMooringsCustomResponse.setCustomerResponseDto(customerResponseDto);
            customerAndMooringsCustomResponse.setBoatyardNames(boatyardNames);

            response.setContent(customerAndMooringsCustomResponse);
            response.setTime(new Timestamp(System.currentTimeMillis()));
            response.setMessage("Customer and its moorings fetched successfully");
            response.setStatus(HttpStatus.OK.value());

        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setErrorList(List.of(e.getCause().toString()));
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
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

                return customerMapper.toDto(CustomerDto.builder().build(), customerEntityOptional.get());

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
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.OK.value());
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
            customerMapper.mapToCustomer(customer, customerRequestDto);

            Optional<Mooring> optionalMooring = mooringRepository.findByMooringId(customerRequestDto.getMooringRequestDto().getMooringId());

            Mooring mooring = null;

            mooring = (optionalMooring.isPresent()) ?
                    mooringService.performSave(customerRequestDto.getMooringRequestDto(), optionalMooring.get(), optionalMooring.get().getId()) :
                    mooringService.performSave(customerRequestDto.getMooringRequestDto(), Mooring.builder().build(), null);

            List<Mooring> mooringList = new ArrayList<>();

            if (null == id) {
                customer.setLastModifiedDate(new Date(System.currentTimeMillis()));
                customer.setCreationDate(new Date());

                mooringList.add(mooring);
            } else {
                mooringList = customer.getMooringList();

                List<Mooring> mooringExist = mooringList.stream().filter(mooring1 -> mooring1.getMooringId().equals(customerRequestDto.getMooringRequestDto().getMooringId())).toList();

                if(mooringList.isEmpty() || mooringExist.isEmpty()) mooringList.add(mooring);
            }

            customer.setMooringList(mooringList);
            customer.setLastModifiedDate(new Date());

            mooring.setCustomer(customerRepository.save(customer));

            mooringRepository.save(mooring);

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
        }
        return response;
    }


}
