package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.CustomerMapper;
import com.marinamooringmanagement.mapper.MooringMapper;
import com.marinamooringmanagement.mapper.MooringStatusMapper;
import com.marinamooringmanagement.model.dto.CustomerDto;
import com.marinamooringmanagement.model.dto.MooringStatusDto;
import com.marinamooringmanagement.model.entity.Customer;
import com.marinamooringmanagement.model.entity.Mooring;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.CustomerRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.CustomerAndMooringsCustomResponse;
import com.marinamooringmanagement.model.response.CustomerResponseDto;
import com.marinamooringmanagement.model.response.MooringResponseDto;
import com.marinamooringmanagement.repositories.CustomerRepository;
import com.marinamooringmanagement.repositories.MooringRepository;
import com.marinamooringmanagement.service.CustomerService;
import com.marinamooringmanagement.utils.SortUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    private MooringStatusMapper mooringStatusMapper;

    @Autowired
    private SortUtils sortUtils;

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

            if (null == customerRequestDto.getMooringRequestDto())
                throw new RuntimeException("Mooring Request not provided");

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
     * Fetches a list of customers based on the provided search request parameters and search text.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @param searchText the text used to search for specific customers by name, email, or other relevant criteria.
     * @return a BasicRestResponse containing the results of the customer search.
     */
    @Override
    public BasicRestResponse fetchCustomers(final BaseSearchRequest baseSearchRequest, final String searchText) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            Specification<Customer> spec = new Specification<Customer>() {
                @Override
                public Predicate toPredicate(Root<Customer> customer, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if(null != searchText) {
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(customer.get("emailAddress"), "%" + searchText + "%"),
                                criteriaBuilder.like(customer.get("phone"), "%" + searchText + "%")
                        ));
                    }
                    return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };

            final Pageable p = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    sortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir())
            );
            final Page<Customer> customerList = customerRepository.findAll(spec, p);

            final List<CustomerResponseDto> customerResponseDtoList = customerList
                    .getContent()
                    .stream()
                    .map(customer -> customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), customer))
                    .toList();

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
    public BasicRestResponse fetchCustomerAndMoorings(final Integer customerId) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            CustomerAndMooringsCustomResponse customerAndMooringsCustomResponse = CustomerAndMooringsCustomResponse.builder().build();
            Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

            if (optionalCustomer.isEmpty()) throw new ResourceNotFoundException("No customer found with the given ID");

            CustomerResponseDto customerResponseDto = CustomerResponseDto.builder().build();
            customerMapper.mapToCustomerResponseDto(customerResponseDto, optionalCustomer.get());

            List<Mooring> mooringList = optionalCustomer.get().getMooringList();
            List<String> boatyardNames = new ArrayList<>();
            List<MooringResponseDto> mooringResponseDtoList = new ArrayList<>();

            if (!mooringList.isEmpty()) {
                boatyardNames = mooringList.stream().map(Mooring::getBoatyardName).toList();
                mooringResponseDtoList = mooringList.stream().map(mooring -> {
                    MooringResponseDto mooringResponseDto = mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), mooring);
                    mooringResponseDto.setMooringStatusDto(mooringStatusMapper.mapToMooringStatusDto(MooringStatusDto.builder().build(), mooring.getMooringStatus()));
                    return mooringResponseDto;
                }).toList();
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
    public CustomerDto getById(final Integer id) {
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
            if (null == customerRequestDto.getId())
                throw new RuntimeException(String.format("Update attempt without a Customer ID provided in the request DTO"));

            Optional<Customer> optionalCustomer = customerRepository.findById(id);

            if (optionalCustomer.isEmpty())
                throw new ResourceNotFoundException(String.format("Customer not found with id: %1$s", id));

            Customer customer = optionalCustomer.get();
            performSave(customerRequestDto, customer, customerRequestDto.getId());
            response.setMessage("Customer with the given customer id updated successfully!!!");
            response.setStatus(HttpStatus.OK.value());

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
            customer.setLastModifiedDate(new Date(System.currentTimeMillis()));
            customerMapper.mapToCustomer(customer, customerRequestDto);
            if (null == id) customer.setCreationDate(new Date());
            Customer savedCustomer =  customerRepository.save(customer);
            Optional<Mooring> optionalMooring = mooringRepository.findByMooringId(customerRequestDto.getMooringRequestDto().getMooringId());
            Mooring mooring = null;
            if(optionalMooring.isPresent()) {
                optionalMooring.get().setCustomer(savedCustomer);
                mooring = mooringService.performSave(customerRequestDto.getMooringRequestDto(), optionalMooring.get(), optionalMooring.get().getId());
            } else {
                mooring = mooringService
                        .performSave(
                                customerRequestDto.getMooringRequestDto(),
                                Mooring.builder().customerName(savedCustomer.getCustomerName()).customer(savedCustomer).build(),
                                null
                        );
            }
            List<Mooring> mooringList = null;
            mooringList = customer.getMooringList();
            if(null == mooringList) mooringList = new ArrayList<>();
            mooringList.add(mooring);
            savedCustomer.setMooringList(mooringList);
            savedCustomer.setLastModifiedDate(new Date());
            customerRepository.save(savedCustomer);
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
    @Transactional
    public BasicRestResponse deleteCustomerById(final Integer id) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        try {
            Optional<Customer> optionalCustomer = customerRepository.findById(id);
            if(optionalCustomer.isEmpty()) throw new ResourceNotFoundException(String.format("No customer found with the given id: %1$s", id));
            Customer customer = optionalCustomer.get();
            List<Mooring> mooringList = customer.getMooringList();

            if(!mooringList.isEmpty()) {
                mooringRepository.saveAll(mooringList.stream().peek(mooring -> mooring.setCustomer(null)).toList());
            }

            customer.setMooringList(new ArrayList<>());
            customerRepository.save(customer);
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
