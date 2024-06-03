package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.CustomerMapper;
import com.marinamooringmanagement.mapper.MooringMapper;
import com.marinamooringmanagement.mapper.MooringStatusMapper;
import com.marinamooringmanagement.model.dto.CustomerDto;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.CustomerRequestDto;
import com.marinamooringmanagement.model.response.*;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.security.config.LoggedInUserUtil;
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

    @Autowired
    private LoggedInUserUtil loggedInUserUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CountryRepository countryRepository;

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
     * @param searchText        the text used to search for specific customers by name, email, or other relevant criteria.
     * @return a BasicRestResponse containing the results of the customer search.
     */
    @Override
    public BasicRestResponse fetchCustomers(final BaseSearchRequest baseSearchRequest, final String searchText, final Integer customerOwnerId) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();
            final Integer loggedInUserId = loggedInUserUtil.getLoggedInUserID();

            Specification<Customer> spec = new Specification<Customer>() {
                @Override
                public Predicate toPredicate(Root<Customer> customer, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if (null != searchText) {
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(customer.get("emailAddress"), "%" + searchText + "%"),
                                criteriaBuilder.like(customer.get("phone"), "%" + searchText + "%")
                        ));
                    }

                    if (loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
                        if (null == customerOwnerId) throw new RuntimeException("Please select a customer owner");
                        predicates.add(criteriaBuilder.and(criteriaBuilder.equal(customer.join("user").get("id"), customerOwnerId)));
                    } else if (loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
                        if (null != customerOwnerId && !customerOwnerId.equals(loggedInUserId))
                            throw new RuntimeException("Not authorized to perform operations on boatyards with different customer owner id");
                        predicates.add(criteriaBuilder.and(criteriaBuilder.equal(customer.join("user").get("id"), loggedInUserId)));
                    } else {
                        throw new RuntimeException("Not Authorized");
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
                    .map(customer -> {
                        CustomerResponseDto customerResponseDto = customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), customer);
                        customerResponseDto.setUserId(customer.getUser().getId());

                        StateResponseDto stateResponseDto = null;

                        if (null != customer.getState()) {
                            stateResponseDto = StateResponseDto.builder()
                                    .id(customer.getState().getId())
                                    .name(customer.getState().getName())
                                    .label(customer.getState().getLabel())
                                    .build();
                        }

                        CountryResponseDto countryResponseDto = null;

                        if (null != customer.getState()) {
                            countryResponseDto = CountryResponseDto.builder()
                                    .id(customer.getCountry().getId())
                                    .name(customer.getCountry().getName())
                                    .label(customer.getCountry().getLabel())
                                    .build();
                        }

                        customerResponseDto.setStateResponseDto(stateResponseDto);
                        customerResponseDto.setCountryResponseDto(countryResponseDto);

                        return customerResponseDto;
                    })
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

            final Customer customer = optionalCustomer.get();

            CustomerResponseDto customerResponseDto = CustomerResponseDto.builder().build();
            customerMapper.mapToCustomerResponseDto(customerResponseDto, customer);
            if (null != customer.getState()) customerResponseDto.setStateResponseDto(
                    StateResponseDto.builder()
                            .id(customer.getState().getId())
                            .name(customer.getState().getName())
                            .label(customer.getState().getLabel())
                            .build()
            );
            if (null != customer.getCountry()) customerResponseDto.setCountryResponseDto(
                    CountryResponseDto.builder()
                            .id(customer.getCountry().getId())
                            .name(customer.getCountry().getName())
                            .label(customer.getCountry().getLabel())
                            .build()
            );

            List<Mooring> mooringList = optionalCustomer.get().getMooringList();
            List<String> boatyardNames = new ArrayList<>();
            List<MooringResponseDto> mooringResponseDtoList = new ArrayList<>();

            if (!mooringList.isEmpty()) {
                mooringResponseDtoList = mooringList.stream().map(mooring -> {
                    MooringResponseDto mooringResponseDto = mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), mooring);
                    if (null != mooring.getUser().getId()) mooringResponseDto.setUserId(mooring.getUser().getId());
                    if (null != mooring.getCustomer().getId())
                        mooringResponseDto.setCustomerId(mooring.getCustomer().getId());
                    if (null != mooring.getBoatyardName()) boatyardNames.add(mooring.getBoatyardName());
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
            if (null == id)
                throw new RuntimeException(String.format("Update attempt without a Customer ID provided in the request DTO"));

            Optional<Customer> optionalCustomer = customerRepository.findById(id);

            if (optionalCustomer.isEmpty())
                throw new ResourceNotFoundException(String.format("Customer not found with id: %1$s", id));

            Customer customer = optionalCustomer.get();
            performSave(customerRequestDto, customer, id);
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
    @Transactional
    public void performSave(final CustomerRequestDto customerRequestDto, final Customer customer, final Integer id) {

        Customer savedCustomer = null;

        try {
            final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();
            final Integer loggedInUserId = loggedInUserUtil.getLoggedInUserID();


            if (null == customerRequestDto.getMooringRequestDto())
                throw new RuntimeException("No mooring details found.");
            if (null == customerRequestDto.getCustomerOwnerId() || null == customerRequestDto.getMooringRequestDto().getCustomerOwnerId()) {
                throw new RuntimeException("Please select a customer owner");
            } else {
                if (!customerRequestDto.getCustomerOwnerId().equals(customerRequestDto.getMooringRequestDto().getCustomerOwnerId())) {
                    throw new RuntimeException(String.format("Customer owner Id are different in customer: %1$s and in mooring: %2$s", customerRequestDto.getCustomerOwnerId(), customerRequestDto.getMooringRequestDto().getCustomerOwnerId()));
                }
            }


            User user = null;
            if (loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
                Optional<User> optionalUser = userRepository.findById(customerRequestDto.getCustomerOwnerId());
                if (optionalUser.isEmpty())
                    throw new ResourceNotFoundException(String.format("No user found with the given id: %1$s", customerRequestDto.getCustomerOwnerId()));
                if (!optionalUser.get().getRole().getName().equals(AppConstants.Role.CUSTOMER_OWNER))
                    throw new RuntimeException(String.format("User with the given id: %1$s is not of Customer Owner role.", customerRequestDto.getCustomerOwnerId()));
                user = optionalUser.get();
            } else if (loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
                if (!loggedInUserId.equals(customerRequestDto.getCustomerOwnerId()))
                    throw new RuntimeException("Cannot do operations on customer with different customer owner Id");
                Optional<User> optionalUser = userRepository.findById(loggedInUserId);
                if (optionalUser.isEmpty())
                    throw new ResourceNotFoundException(String.format("No user found with the given id: %1$s", customerRequestDto.getCustomerOwnerId()));
                user = optionalUser.get();
            } else {
                throw new RuntimeException("Not Authorized");
            }

            if (null != customerRequestDto.getEmailAddress()) {
                Optional<Customer> optionalCustomer = customerRepository.findByEmailAddress(customerRequestDto.getEmailAddress());
                if (optionalCustomer.isPresent()) {
                    if (null == id) {
                        throw new RuntimeException(String.format("Given email address: %1$s  is already present", customerRequestDto.getEmailAddress()));
                    } else {
                        if (!optionalCustomer.get().getId().equals(id))
                            throw new RuntimeException(String.format("Given email address: %1$s  is already present", customerRequestDto.getEmailAddress()));
                    }
                }
            }

            if (null != customerRequestDto.getCustomerId()) {
                Optional<Customer> optionalCustomer = customerRepository.findByCustomerId(customerRequestDto.getCustomerId());
                if (optionalCustomer.isPresent()) {
                    if (null == id) {
                        throw new RuntimeException(String.format("Given customer id: %1$s  is already present", customerRequestDto.getCustomerId()));
                    } else {
                        if (!optionalCustomer.get().getId().equals(id))
                            throw new RuntimeException(String.format("Given customer id: %1$s  is already present", customerRequestDto.getCustomerId()));
                    }
                }
            }

            customer.setUser(user);

            customer.setLastModifiedDate(new Date(System.currentTimeMillis()));
            customerMapper.mapToCustomer(customer, customerRequestDto);

            if (null != customerRequestDto.getStateId()) {
                final Optional<State> optionalState = stateRepository.findById(customerRequestDto.getStateId());
                if (optionalState.isEmpty())
                    throw new ResourceNotFoundException(String.format("No state found with the given Id: %1$s", customerRequestDto.getStateId()));
                customer.setState(optionalState.get());
            } else {
                if (null == id) throw new RuntimeException("State cannot be null.");
            }

            if (null != customerRequestDto.getCountryId()) {
                final Optional<Country> optionalCountry = countryRepository.findById(customerRequestDto.getCountryId());
                if (optionalCountry.isEmpty())
                    throw new ResourceNotFoundException(String.format("No country found with the given Id: %1$s", customerRequestDto.getCountryId()));
                customer.setCountry(optionalCountry.get());
            } else {
                if (null == id) throw new RuntimeException("Country cannot be null.");
            }

            if (null == id) customer.setCreationDate(new Date());
            savedCustomer = customerRepository.save(customer);

            customerRequestDto.getMooringRequestDto().setCustomerId(savedCustomer.getId());
            Optional<Mooring> optionalMooring = Optional.empty();
            if (null != customerRequestDto.getMooringRequestDto().getMooringId()) {
                optionalMooring = mooringRepository.findByMooringId(customerRequestDto.getMooringRequestDto().getMooringId());
                if (optionalMooring.isPresent()) {
                    if (null == id) {
                        throw new RuntimeException(String.format("Given mooring Id: %1$s is already present", customerRequestDto.getMooringRequestDto().getMooringId()));
                    } else {
                        if (!optionalMooring.get().getId().equals(customerRequestDto.getMooringRequestDto().getId()))
                            throw new RuntimeException(String.format("Given mooring Id: %1$s is already present", customerRequestDto.getMooringRequestDto().getMooringId()));
                    }
                }
            } else {
                throw new RuntimeException("Mooring Id cannot be null");
            }

            if (null != customerRequestDto.getMooringRequestDto().getMooringId())
                optionalMooring = mooringRepository.findByMooringId(customerRequestDto.getMooringRequestDto().getMooringId());
            Mooring mooring = null;
            if (optionalMooring.isPresent()) {
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
            List<Mooring> mooringList = customer.getMooringList();
            if (null == mooringList || mooringList.isEmpty()) {
                mooringList = new ArrayList<>();
                mooringList.add(mooring);
            }
            savedCustomer.setMooringList(mooringList);

            savedCustomer.setLastModifiedDate(new Date());
            customerRepository.save(savedCustomer);
            log.info(String.format("Customer saved successfully with ID: %d", customer.getId()));
        } catch (Exception e) {
            if(id == null && null != savedCustomer) customerRepository.deleteById(savedCustomer.getId());
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
            final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();
            final Integer loggedInUserID = loggedInUserUtil.getLoggedInUserID();

            Optional<Customer> optionalCustomer = customerRepository.findById(id);
            if (optionalCustomer.isEmpty())
                throw new ResourceNotFoundException(String.format("No customer found with the given id: %1$s", id));
            Customer customer = optionalCustomer.get();
            List<Mooring> mooringList = customer.getMooringList();

            if (loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
                if (!optionalCustomer.get().getUser().getId().equals(loggedInUserID))
                    throw new RuntimeException("Not authorized to perform operations on mooring with different customer owner Id");
            } else if (loggedInUserRole.equals(AppConstants.Role.FINANCE) || loggedInUserRole.equals(AppConstants.Role.TECHNICIAN)) {
                throw new RuntimeException("Not Authorized");
            }

            if (!mooringList.isEmpty()) {
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
