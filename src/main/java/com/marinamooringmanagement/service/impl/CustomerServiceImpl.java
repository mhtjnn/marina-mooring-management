package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.*;
import com.marinamooringmanagement.mapper.metadata.CountryMapper;
import com.marinamooringmanagement.mapper.metadata.CustomerTypeMapper;
import com.marinamooringmanagement.mapper.metadata.MooringStatusMapper;
import com.marinamooringmanagement.mapper.metadata.StateMapper;
import com.marinamooringmanagement.model.dto.CustomerDto;
import com.marinamooringmanagement.model.dto.metadata.CustomerTypeDto;
import com.marinamooringmanagement.model.dto.ImageDto;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.entity.metadata.Country;
import com.marinamooringmanagement.model.entity.metadata.CustomerType;
import com.marinamooringmanagement.model.entity.metadata.State;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.CustomerRequestDto;
import com.marinamooringmanagement.model.request.ImageRequestDto;
import com.marinamooringmanagement.model.request.MooringRequestDto;
import com.marinamooringmanagement.model.response.*;
import com.marinamooringmanagement.model.response.metadata.CountryResponseDto;
import com.marinamooringmanagement.model.response.metadata.StateResponseDto;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.repositories.metadata.CountryRepository;
import com.marinamooringmanagement.repositories.metadata.CustomerTypeRepository;
import com.marinamooringmanagement.repositories.metadata.StateRepository;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.CustomerService;
import com.marinamooringmanagement.utils.DateUtil;
import com.marinamooringmanagement.utils.ImageUtils;
import com.marinamooringmanagement.utils.SortUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private MooringMapper mooringMapper;

    @Autowired
    private MooringRepository mooringRepository;

    @Autowired
    private MooringServiceImpl mooringService;

    @Autowired
    private MooringStatusMapper mooringStatusMapper;

    @Autowired
    private LoggedInUserUtil loggedInUserUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private BoatyardMapper boatyardMapper;

    @Autowired
    private StateMapper stateMapper;

    @Autowired
    private CountryMapper countryMapper;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private CustomerTypeRepository customerTypeRepository;

    @Autowired
    private CustomerTypeMapper customerTypeMapper;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    @Autowired
    private QuickbookCustomerMapper quickbookCustomerMapper;

    @Autowired
    private ImageRepository imageRepository;

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    /**
     * Saves a new customer.
     *
     * @param customerRequestDto The DTO containing customer information.
     */
    @Override
    @Transactional
    public BasicRestResponse saveCustomer(final CustomerRequestDto customerRequestDto, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        try {
            log.info(String.format("Save customer method called"));

            final Customer customer = Customer.builder().build();

            performSave(customerRequestDto, customer, null, request);

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
    @Transactional
    public BasicRestResponse fetchCustomers(final BaseSearchRequest baseSearchRequest, final String searchText, final HttpServletRequest request) {

        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            final Pageable pageable = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    SortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir())
            );
            final List<Customer> customerList = customerRepository.findAll((null == searchText) ? "" : searchText, user.getId());
            response.setTotalSize(customerList.size());

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), customerList.size());

            List<Customer> paginatedCustomer;
            if (start > customerList.size()) {
                paginatedCustomer = new ArrayList<>();
            } else {
                paginatedCustomer = customerList.subList(start, end);
            }

            final List<CustomerResponseDto> customerResponseDtoList = paginatedCustomer
                    .stream()
                    .map(customer -> {
                        CustomerResponseDto customerResponseDto = customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), customer);

                        if (null != customer.getUser()) customerResponseDto.setUserId(customer.getUser().getId());
                        if (null != customer.getState())
                            customerResponseDto.setStateResponseDto(stateMapper.mapToStateResponseDto(StateResponseDto.builder().build(), customer.getState()));
                        if (null != customer.getCountry())
                            customerResponseDto.setCountryResponseDto(countryMapper.mapToCountryResponseDto(CountryResponseDto.builder().build(), customer.getCountry()));
                        if(null != customer.getCustomerType())
                            customerResponseDto.setCustomerTypeDto(customerTypeMapper.toDto(CustomerTypeDto.builder().build(), customer.getCustomerType()));

                        if(null != customer.getQuickBookCustomer()) {
                            customerResponseDto.setQuickbookCustomerResponseDto(quickbookCustomerMapper.mapToResponseDto(QuickbookCustomerResponseDto.builder().build(), customer.getQuickBookCustomer()));
                        }
                        return customerResponseDto;
                    })
                    .toList();

            response.setContent(customerResponseDtoList);

            response.setCurrentSize(customerResponseDtoList.size());

            response.setMessage("All customers are fetched successfully");
            response.setStatus(HttpStatus.OK.value());

            log.info(String.format("Customers fetched successfully"));

        } catch (Exception e) {
            log.error(String.format("Error occurred while fetching Customers: %s", e.getMessage()), e);
            throw e;
        }

        return response;
    }

    @Override
    @Transactional
    public BasicRestResponse fetchCustomerAndMooringsWithCustomerImages(final BaseSearchRequest baseSearchRequest, final Integer customerId, final HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            CustomerAndMooringsCustomResponse customerAndMooringsCustomResponse = CustomerAndMooringsCustomResponse.builder().build();
            Optional<Customer> optionalCustomer = customerRepository.findCustomerByIdWithImages(customerId, user.getId());

            if (optionalCustomer.isEmpty())
                throw new ResourceNotFoundException(String.format("No customer found with the given ID: %1$s", customerId));

            final Customer customer = optionalCustomer.get();


            if (null != customer.getUser()) {
                if (!customer.getUser().getId().equals(user.getId()))
                    throw new RuntimeException(String.format("Customer with the id: %1$s is associated with some other user", customerId));
            } else {
                throw new RuntimeException(String.format("Customer with the id: %1$s is not associated with any User", customerId));
            }

            CustomerResponseDto customerResponseDto = CustomerResponseDto.builder().build();
            customerMapper.mapToCustomerResponseDto(customerResponseDto, customer);
            if (null != customer.getState()) customerResponseDto.setStateResponseDto(
                    stateMapper.mapToStateResponseDto(StateResponseDto.builder().build(), customer.getState())
            );
            if (null != customer.getCountry()) customerResponseDto.setCountryResponseDto(
                    countryMapper.mapToCountryResponseDto(CountryResponseDto.builder().build(), customer.getCountry())
            );
            if(null != customer.getCustomerType())
                customerResponseDto.setCustomerTypeDto(customerTypeMapper.toDto(CustomerTypeDto.builder().build(), customer.getCustomerType()));

            if(null != customer.getImageList() && !customer.getImageList().isEmpty()) {
                customerResponseDto.setImageDtoList(customer.getImageList()
                        .stream()
                        .map(image -> imageMapper.toDto(ImageDto.builder().build(), image))
                        .toList());
            }

            List<Mooring> mooringList = new ArrayList<>();

            mooringList = mooringRepository.fetchMooringsWithCustomerWithoutMooringImages(customer.getId(), user.getId());

            if(mooringList.isEmpty()) response.setTotalSize(0);
            else response.setTotalSize(mooringList.size());

            List<Boatyard> boatyards = new ArrayList<>();
            List<String> boatyardNames = new ArrayList<>();
            List<MooringResponseDto> mooringResponseDtoList = new ArrayList<>();

            final Pageable p = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    SortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir())
            );

            int start = (int) p.getOffset();
            int end = Math.min((start + p.getPageSize()), mooringList.size());

            List<Mooring> paginatedMoorings;
            if (start > mooringList.size()) {
                paginatedMoorings = List.of(); // Return an empty list if the start index is out of bounds
            } else {
                paginatedMoorings = mooringList.subList(start, end);
            }

            if (!mooringList.isEmpty()) {
                mooringResponseDtoList = paginatedMoorings
                        .stream()
                        .map(mooring -> {
                            MooringResponseDto mooringResponseDto = mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), mooring);
                            if (null != mooring.getUser().getId())
                                mooringResponseDto.setUserId(mooring.getUser().getId());
                            if (null != mooring.getCustomer())
                                mooringResponseDto.setCustomerId(mooring.getCustomer().getId());
                            if (null != mooring.getBoatyard()) {
                                mooringResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), mooring.getBoatyard()));
                                if (!boatyards.contains(mooring.getBoatyard())) {
                                    boatyards.add(mooring.getBoatyard());
                                    if(null != mooring.getBoatyard().getBoatyardName()) boatyardNames.add(mooring.getBoatyard().getBoatyardName());
                                }
                            }
                            if(null != customerResponseDto.getFirstName() && null != customerResponseDto.getLastName()) mooringResponseDto.setCustomerName(
                                    customerResponseDto.getFirstName() + " " + customerResponseDto.getLastName()
                            );
                            if(null != mooring.getServiceArea()) mooringResponseDto.setServiceAreaResponseDto(serviceAreaMapper.mapToResponseDto(ServiceAreaResponseDto.builder().build(), mooring.getServiceArea()));
                            if(null != mooring.getInstallBottomChainDate()) mooringResponseDto.setInstallBottomChainDate(DateUtil.dateToString(mooring.getInstallBottomChainDate()));
                            if(null != mooring.getInstallTopChainDate()) mooringResponseDto.setInstallTopChainDate(DateUtil.dateToString(mooring.getInstallTopChainDate()));
                            if(null != mooring.getInstallConditionOfEyeDate()) mooringResponseDto.setInstallConditionOfEyeDate(DateUtil.dateToString(mooring.getInstallConditionOfEyeDate()));
                            if(null != mooring.getInspectionDate()) mooringResponseDto.setInspectionDate(DateUtil.dateToString(mooring.getInspectionDate()));
                            if(null != mooring.getImageList() && !mooring.getImageList().isEmpty()) {
                                mooringResponseDto.setImageDtoList(mooring.getImageList()
                                        .stream()
                                        .map(image -> imageMapper.toDto(ImageDto.builder().build(), image))
                                        .toList());
                            }
                            return mooringResponseDto;
                        }).toList();
            }

            if(mooringResponseDtoList.isEmpty()) response.setCurrentSize(0);
            else response.setCurrentSize(mooringResponseDtoList.size());

            boatyards.clear();

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

    @Override
    @Transactional
    public BasicRestResponse fetchCustomerAndMooringsWithMooringImages(final BaseSearchRequest baseSearchRequest, final Integer customerId, final HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            CustomerAndMooringsCustomResponse customerAndMooringsCustomResponse = CustomerAndMooringsCustomResponse.builder().build();
            Optional<Customer> optionalCustomer = customerRepository.findCustomerByIdWithoutImages(customerId, user.getId());

            if (optionalCustomer.isEmpty())
                throw new ResourceNotFoundException(String.format("No customer found with the given ID: %1$s", customerId));

            final Customer customer = optionalCustomer.get();

            if (null != customer.getUser()) {
                if (!customer.getUser().getId().equals(user.getId()))
                    throw new RuntimeException(String.format("Customer with the id: %1$s is associated with some other user", customerId));
            } else {
                throw new RuntimeException(String.format("Customer with the id: %1$s is not associated with any User", customerId));
            }

            CustomerResponseDto customerResponseDto = CustomerResponseDto.builder().build();
            customerMapper.mapToCustomerResponseDto(customerResponseDto, customer);
            if (null != customer.getState()) customerResponseDto.setStateResponseDto(
                    stateMapper.mapToStateResponseDto(StateResponseDto.builder().build(), customer.getState())
            );
            if (null != customer.getCountry()) customerResponseDto.setCountryResponseDto(
                    countryMapper.mapToCountryResponseDto(CountryResponseDto.builder().build(), customer.getCountry())
            );
            if(null != customer.getCustomerType())
                customerResponseDto.setCustomerTypeDto(customerTypeMapper.toDto(CustomerTypeDto.builder().build(), customer.getCustomerType()));

            if(null != customer.getImageList() && !customer.getImageList().isEmpty()) {
                customerResponseDto.setImageDtoList(customer.getImageList()
                        .stream()
                        .map(image -> imageMapper.toDto(ImageDto.builder().build(), image))
                        .toList());
            }

            List<Mooring> mooringList = mooringRepository.fetchMooringsWithCustomerWithoutMooringImages(customer.getId(), user.getId());

            if(mooringList.isEmpty()) response.setTotalSize(0);
            else response.setTotalSize(mooringList.size());

            List<Boatyard> boatyards = new ArrayList<>();
            List<String> boatyardNames = new ArrayList<>();
            List<MooringResponseDto> mooringResponseDtoList = new ArrayList<>();

            final Pageable p = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    SortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir())
            );

            int start = (int) p.getOffset();
            int end = Math.min((start + p.getPageSize()), mooringList.size());

            List<Mooring> paginatedMoorings;
            if (start > mooringList.size()) {
                paginatedMoorings = List.of(); // Return an empty list if the start index is out of bounds
            } else {
                paginatedMoorings = mooringList.subList(start, end);
            }

            if (!mooringList.isEmpty()) {
                mooringResponseDtoList = paginatedMoorings
                        .stream()
                        .map(mooring -> {

                            List<Image> imageList = imageRepository.findImagesByMooringId(mooring.getId());

                            MooringResponseDto mooringResponseDto = mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), mooring);
                            if (null != mooring.getUser().getId())
                                mooringResponseDto.setUserId(mooring.getUser().getId());
                            if (null != mooring.getCustomer())
                                mooringResponseDto.setCustomerId(mooring.getCustomer().getId());
                            if (null != mooring.getBoatyard()) {
                                mooringResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), mooring.getBoatyard()));
                                if (!boatyards.contains(mooring.getBoatyard())) {
                                    boatyards.add(mooring.getBoatyard());
                                    boatyardNames.add(mooring.getBoatyard().getBoatyardName());
                                }
                            }
                            if(null != customerResponseDto.getFirstName() && null != customerResponseDto.getLastName()) mooringResponseDto.setCustomerName(
                                    customerResponseDto.getFirstName() + " " + customerResponseDto.getLastName()
                            );
                            if(null != mooring.getServiceArea()) mooringResponseDto.setServiceAreaResponseDto(serviceAreaMapper.mapToResponseDto(ServiceAreaResponseDto.builder().build(), mooring.getServiceArea()));
                            if(null != mooring.getInstallBottomChainDate()) mooringResponseDto.setInstallBottomChainDate(DateUtil.dateToString(mooring.getInstallBottomChainDate()));
                            if(null != mooring.getInstallTopChainDate()) mooringResponseDto.setInstallTopChainDate(DateUtil.dateToString(mooring.getInstallTopChainDate()));
                            if(null != mooring.getInstallConditionOfEyeDate()) mooringResponseDto.setInstallConditionOfEyeDate(DateUtil.dateToString(mooring.getInstallConditionOfEyeDate()));
                            if(null != mooring.getInspectionDate()) mooringResponseDto.setInspectionDate(DateUtil.dateToString(mooring.getInspectionDate()));
                            if(null != imageList && !imageList.isEmpty()) {
                                mooringResponseDto.setImageDtoList(imageList
                                        .stream()
                                        .map(image -> imageMapper.toDto(ImageDto.builder().build(), image))
                                        .toList());
                            }
                            return mooringResponseDto;
                        }).toList();
            }

            if(mooringResponseDtoList.isEmpty()) response.setCurrentSize(0);
            else response.setCurrentSize(mooringResponseDtoList.size());

            boatyards.clear();

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
    @Transactional
    public BasicRestResponse updateCustomer(final CustomerRequestDto customerRequestDto, final Integer id, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            if (null == id)
                throw new RuntimeException(String.format("Update attempt without a Customer ID provided in the request DTO"));

            Optional<Customer> optionalCustomer = customerRepository.findById(id);

            if (optionalCustomer.isEmpty())
                throw new ResourceNotFoundException(String.format("Customer not found with id: %1$s", id));

            Customer customer = optionalCustomer.get();
            performSave(customerRequestDto, customer, id, request);
            response.setMessage(String.format("Customer with the given customer id: %1$s updated successfully!!!", id));
            response.setStatus(HttpStatus.OK.value());

        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
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
    public void performSave(final CustomerRequestDto customerRequestDto, final Customer customer, final Integer id, final HttpServletRequest request) {

        Customer savedCustomer = null;

        try {

            Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);

            User user = authorizationUtil.checkAuthority(customerOwnerId);

            final Customer initialCustomer = copyCustomer(customer);

            if(id == null) {
                if(null == customerRequestDto.getFirstName()) throw new RuntimeException("First name cannot be null during save");
                if(null == customerRequestDto.getLastName()) throw new RuntimeException("Last name cannot be null during save");
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

            if (null == id) {
                if (StringUtils.isEmpty(customerRequestDto.getLastName()))
                    throw new RuntimeException("Last name cannot be null");
                StringBuilder customerId = createCustomerId(customerRequestDto.getLastName());
                String customerIdStr = customerId.toString();
                Optional<Customer> optionalCustomer = customerRepository.findByCustomerId(customerIdStr);

                while (optionalCustomer.isPresent()) {
                    customerId = createCustomerId(customerRequestDto.getLastName());
                    customerIdStr = customerId.toString();
                    optionalCustomer = customerRepository.findByCustomerId(customerIdStr);
                }

                customer.setCustomerId(customerIdStr);
            }

            customer.setUser(user);

            customer.setLastModifiedDate(new Date(System.currentTimeMillis()));
            customerMapper.mapToCustomer(customer, customerRequestDto);

            if (null == id) customer.setCreationDate(new Date());
            savedCustomer = customerRepository.save(customer);

            if(null != customerRequestDto.getImageRequestDtoList() && !customerRequestDto.getImageRequestDtoList().isEmpty()) {
                List<Image> imageList = new ArrayList<>();
                if(null != customer.getImageList() && !customer.getImageList().isEmpty()) imageList = customer.getImageList();
                Integer imageNumber = 1;
                for(ImageRequestDto imageRequestDto: customerRequestDto.getImageRequestDtoList()) {
                    Image image = imageMapper.toEntity(Image.builder().build(), imageRequestDto);

                    if(null == imageRequestDto.getImageName()) throw new RuntimeException(String.format("No name provided for image at number: %1$s", imageNumber));
                    if(null == imageRequestDto.getImageData()) throw new RuntimeException(String.format("No image provided for: %1$s", imageRequestDto.getImageName()));

                    image.setImageData(ImageUtils.validateEncodedString(imageRequestDto.getImageData()));
                    image.setCreationDate(new Date(System.currentTimeMillis()));
                    image.setLastModifiedDate(new Date(System.currentTimeMillis()));
                    image.setCustomer(savedCustomer);
                    imageList.add(image);
                    imageNumber++;
                }
                imageRepository.saveAll(imageList);
            }

            if (null != customerRequestDto.getStateId()) {
                final Optional<State> optionalState = stateRepository.findById(customerRequestDto.getStateId());
                if (optionalState.isEmpty())
                    throw new ResourceNotFoundException(String.format("No state found with the given Id: %1$s", customerRequestDto.getStateId()));
                customer.setState(optionalState.get());
            }

            if (null != customerRequestDto.getCountryId()) {
                final Optional<Country> optionalCountry = countryRepository.findById(customerRequestDto.getCountryId());
                if (optionalCountry.isEmpty())
                    throw new ResourceNotFoundException(String.format("No country found with the given Id: %1$s", customerRequestDto.getCountryId()));
                customer.setCountry(optionalCountry.get());
            }

            if(null != customerRequestDto.getCustomerTypeId()) {
                final Optional<CustomerType> optionalCustomerType = customerTypeRepository.findById(customerRequestDto.getCustomerTypeId());
                if(optionalCustomerType.isEmpty())
                    throw new RuntimeException(String.format("No customer type found with the given id: %1$s", customerRequestDto.getCustomerTypeId()));
                customer.setCustomerType(optionalCustomerType.get());
            }

            List<Mooring> mooringList = (null == customer.getMooringList()) ? new ArrayList<>() : customer.getMooringList();

            if (null != customerRequestDto.getMooringRequestDtoList()) {
                for (MooringRequestDto mooringRequestDto : customerRequestDto.getMooringRequestDtoList()) {

                    // Setting the customer Id here.
                    if (null == mooringRequestDto.getCustomerId())
                        mooringRequestDto.setCustomerId(savedCustomer.getId());
                    else {
                        if (!mooringRequestDto.getCustomerId().equals(savedCustomer.getId())) {
                            throw new RuntimeException(String.format("Customer Id: %1$s in mooring is different from id: %2$s of the customer.", mooringRequestDto.getCustomerId(), savedCustomer.getId()));
                        }
                    }

                    Optional<Mooring> optionalMooring = Optional.empty();
                    Mooring mooring = null;
                    if (null != mooringRequestDto.getMooringNumber() && !mooringRequestDto.getMooringNumber().isBlank()) {
                        optionalMooring = mooringRepository.findByMooringNumber(mooringRequestDto.getMooringNumber());
                        if (optionalMooring.isPresent()) {
                            mooring = optionalMooring.get();
                            if(null != mooring.getCustomer() && id == null) throw new RuntimeException(String.format("Mooring with number: %1$s is associated with some other customer", mooringRequestDto.getMooringNumber()));
                            if (null == mooring.getUser())
                                throw new RuntimeException(String.format("Mooring with the number: %1$s is not associated with any user", mooring.getMooringNumber()));
                            if (!mooring.getUser().getId().equals(user.getId()))
                                throw new RuntimeException(String.format("Mooring with the number: %1$s is associated with some other customer owner", mooring.getMooringNumber()));
                            mooring.setCustomer(savedCustomer);
                            mooring = mooringService.performSave(
                                    mooringRequestDto,
                                    mooring,
                                    optionalMooring.get().getId(),
                                    request
                            );
                        } else {
                            mooring = mooringService
                                    .performSave(
                                            mooringRequestDto,
                                            Mooring.builder().customer(savedCustomer).build(),
                                            null,
                                            request
                                    );
                        }
                    } else {
                        throw new RuntimeException("Mooring Number cannot be blank");
                    }

                    mooringList.add(mooring);
                }
                savedCustomer.setMooringList(mooringList);
            }
            savedCustomer.setLastModifiedDate(new Date());
            customerRepository.save(savedCustomer);

            customerChangedLogs(initialCustomer, savedCustomer, user);

            log.info(String.format("Customer saved successfully with ID: %d", customer.getId()));
        } catch (Exception e) {
            if (id == null && null != savedCustomer) customerRepository.deleteById(savedCustomer.getId());
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
    public BasicRestResponse deleteCustomerById(final Integer id, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        try {
            Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            if (-1 == customerOwnerId && null != request.getAttribute(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID))
                customerOwnerId = (Integer) request.getAttribute(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);

            Optional<Customer> optionalCustomer = customerRepository.findById(id);
            if (optionalCustomer.isEmpty())
                throw new ResourceNotFoundException(String.format("No customer found with the given id: %1$s", id));
            Customer customer = optionalCustomer.get();
            List<Mooring> mooringList = customer.getMooringList();

            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            if (null != customer.getUser()) {
                if (!customer.getUser().getId().equals(user.getId()))
                    throw new RuntimeException(String.format("Customer with the id: %1$s is associated with some other user", id));
            } else {
                throw new RuntimeException(String.format("Customer with the id: %1$s is not associated with any User", id));
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

    @Transactional
    public Customer fetchCustomerById(final Integer customerId, final HttpServletRequest request) {
        try {
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
            if (optionalCustomer.isEmpty())
                throw new ResourceNotFoundException(String.format("No customer found with the given id: %1$s", customerId));
            final Customer customer = optionalCustomer.get();

            if (null == customer.getUser())
                throw new RuntimeException(String.format("Customer with the id: %1$s is associated with no user", customerId));
            if (!customer.getUser().getId().equals(user.getId()))
                throw new RuntimeException(String.format("Customer with given id: %1$s is associated with some other user", customerId));

            return customer;
        } catch (Exception e) {
            throw e;
        }
    }

    public StringBuilder createCustomerId(final String lastName) {

        if (null == lastName) throw new RuntimeException("Last name cannot be null");
        if (lastName.length() < 3) throw new RuntimeException("Last name should be of 3 or more length");
        final StringBuilder customerId = new StringBuilder();
        customerId.append(lastName.toUpperCase(), 0, 3);
        int randomThreeDigitNumber = 100 + (int) (Math.random() * 900);
        String randomThreeDigitNumberStr = Integer.toString(randomThreeDigitNumber);
        customerId.append(randomThreeDigitNumberStr);

        return customerId;
    }

    @Transactional
    private void customerChangedLogs(final Customer initialCustomer, final Customer savedCustomer, final User user) {
        if(initialCustomer.getId() != null && savedCustomer.getId() != null && !initialCustomer.getId().equals(savedCustomer.getId()))
            log.info(String.format("Customer (Integer) Id changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialCustomer.getId(), savedCustomer.getId(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialCustomer.getFirstName() != null && savedCustomer.getFirstName() != null && !initialCustomer.getFirstName().equals(savedCustomer.getFirstName()))
            log.info(String.format("Customer first name changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialCustomer.getFirstName(), savedCustomer.getFirstName(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialCustomer.getLastName() != null && savedCustomer.getLastName() != null && !initialCustomer.getLastName().equals(savedCustomer.getLastName()))
            log.info(String.format("Customer last name changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialCustomer.getLastName(), savedCustomer.getLastName(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialCustomer.getCustomerId() != null && savedCustomer.getCustomerId() != null && !initialCustomer.getCustomerId().equals(savedCustomer.getCustomerId()))
            log.info(String.format("Customer (String) Id changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialCustomer.getCustomerId(), savedCustomer.getCustomerId(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialCustomer.getPhone() != null && savedCustomer.getPhone() != null && !initialCustomer.getPhone().equals(savedCustomer.getPhone()))
            log.info(String.format("Customer phone number changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialCustomer.getPhone(), savedCustomer.getPhone(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialCustomer.getEmailAddress() != null && savedCustomer.getEmailAddress() != null && !initialCustomer.getEmailAddress().equals(savedCustomer.getEmailAddress()))
            log.info(String.format("Customer email address changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialCustomer.getEmailAddress(), savedCustomer.getEmailAddress(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialCustomer.getImageList() != null && savedCustomer.getImageList() != null && initialCustomer.getImageList() != savedCustomer.getImageList())
            log.info(String.format("Customer image changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialCustomer.getImageList(), savedCustomer.getImageList(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialCustomer.getNotes() != null && savedCustomer.getNotes() != null && !initialCustomer.getNotes().equals(savedCustomer.getNotes()))
            log.info(String.format("Customer note changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialCustomer.getNotes(), savedCustomer.getNotes(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialCustomer.getAddress() != null && savedCustomer.getAddress() != null && !initialCustomer.getAddress().equals(savedCustomer.getAddress()))
            log.info(String.format("Customer street/house changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialCustomer.getAddress(), savedCustomer.getAddress(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialCustomer.getState() != null && savedCustomer.getState() != null && initialCustomer.getState().getName() != null && savedCustomer.getState().getName() != null && !initialCustomer.getState().getName().equals(savedCustomer.getState().getName()))
            log.info(String.format("Customer state changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialCustomer.getState().getName(), savedCustomer.getState().getName(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialCustomer.getCountry() != null && savedCustomer.getCountry() != null && initialCustomer.getCountry().getName() != null && savedCustomer.getCountry().getName() != null && !initialCustomer.getCountry().getName().equals(savedCustomer.getCountry().getName()))
            log.info(String.format("Customer country changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialCustomer.getCountry().getName(), savedCustomer.getCountry().getName(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(initialCustomer.getZipCode() != null && savedCustomer.getZipCode() != null && !initialCustomer.getZipCode().equals(savedCustomer.getZipCode()))
            log.info(String.format("Customer zipcode changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialCustomer.getZipCode(), savedCustomer.getZipCode(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(
                initialCustomer.getCustomerType() != null
                        && savedCustomer.getCustomerType() != null
                        && initialCustomer.getCustomerType().getType() != null
                        && savedCustomer.getCustomerType().getType() != null
                        && !initialCustomer.getCustomerType().getType().equals(savedCustomer.getCustomerType().getType())
        )
            log.info(String.format("Customer type changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialCustomer.getCustomerType().getType(), savedCustomer.getCustomerType().getType(), user.getId(), user.getFirstName() + " " + user.getLastName()));

        if(
                initialCustomer.getUser() != null
                        && savedCustomer.getUser() != null
                        && initialCustomer.getUser().getId() != null
                        && savedCustomer.getUser().getId() != null
                        && !initialCustomer.getUser().getId().equals(savedCustomer.getUser().getId())
        )
            log.info(String.format("Customer customer owner changed from: %1$s to %2$s by user of id: %3$s and name: %4$s", initialCustomer.getUser().getId(), savedCustomer.getUser().getId(), user.getId(), user.getFirstName() + " " + user.getLastName()));

    }

    @Transactional
    private Customer copyCustomer(Customer customer) {

        final Customer copyCustomer = Customer.builder().build();
        if(customer.getId() != null) copyCustomer.setId(customer.getId());
        if(customer.getFirstName() != null) copyCustomer.setFirstName(customer.getFirstName());
        if(customer.getLastName() != null) copyCustomer.setLastName(customer.getLastName());
        if(customer.getCustomerId() != null) copyCustomer.setCustomerId(customer.getCustomerId());
        if(customer.getPhone() != null) copyCustomer.setPhone(customer.getPhone());
        if(customer.getEmailAddress() != null) copyCustomer.setEmailAddress(customer.getEmailAddress());
        if(customer.getImageList() != null) copyCustomer.setImageList(customer.getImageList());
        if(customer.getNotes() != null) copyCustomer.setNotes(customer.getNotes());
        if(customer.getAddress() != null) copyCustomer.setAddress(customer.getAddress());
        if(customer.getState() != null) copyCustomer.setState(customer.getState());
        if(customer.getCountry() != null) copyCustomer.setCountry(customer.getCountry());
        if(customer.getZipCode() != null) copyCustomer.setZipCode(customer.getZipCode());
        if(customer.getCustomerType() != null) copyCustomer.setCustomerType(customer.getCustomerType());
        if(customer.getMooringList() != null) copyCustomer.setMooringList(customer.getMooringList());
        if(customer.getUser() != null) copyCustomer.setUser(customer.getUser());

        return copyCustomer;
    }
}
