package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.CountryMapper;
import com.marinamooringmanagement.mapper.StateMapper;
import com.marinamooringmanagement.mapper.VendorMapper;
import com.marinamooringmanagement.model.entity.Country;
import com.marinamooringmanagement.model.entity.State;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.entity.Vendor;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.VendorRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.CountryResponseDto;
import com.marinamooringmanagement.model.response.StateResponseDto;
import com.marinamooringmanagement.model.response.VendorResponseDto;
import com.marinamooringmanagement.repositories.CountryRepository;
import com.marinamooringmanagement.repositories.StateRepository;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.repositories.VendorRepository;
import com.marinamooringmanagement.security.config.LoggedInUserUtil;
import com.marinamooringmanagement.service.VendorService;
import com.marinamooringmanagement.utils.SortUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for managing Vendor entities.
 */
@Service
public class VendorServiceImpl implements VendorService {

    private static final Logger logger = LoggerFactory.getLogger(VendorServiceImpl.class);

    @Autowired
    private VendorMapper vendorMapper;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private SortUtils sortUtils;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoggedInUserUtil loggedInUserUtil;

    @Autowired
    private StateMapper stateMapper;

    @Autowired
    private CountryMapper countryMapper;

    /**
     * Fetches a list of vendors based on the provided search request parameters and search text.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @param searchText the text used to search for specific vendors by name, location, or other relevant criteria.
     * @return a BasicRestResponse containing the results of the vendor search.
     */
    @Override
    public BasicRestResponse fetchVendors(final BaseSearchRequest baseSearchRequest, final String searchText, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            logger.info("API called to fetch all the vendors from the database");

            final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();
            final Integer loggedInUserId = loggedInUserUtil.getLoggedInUserID();

            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

            Specification<Vendor> spec = new Specification<Vendor>() {
                @Override
                public Predicate toPredicate(Root<Vendor> vendor, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if(null != searchText) {
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(vendor.get("state"), "%"+searchText+"%"),
                                criteriaBuilder.like(vendor.get("companyName"), "%" + searchText + "%"),
                                criteriaBuilder.like(vendor.get("website"), "%" + searchText + "%")
                        ));
                    }

                    if (loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
                        if(customerOwnerId == -1) throw new RuntimeException("Please select a customer owner");
                        predicates.add(criteriaBuilder.and(criteriaBuilder.equal(vendor.join("user").get("id"), customerOwnerId)));
                    } else if (loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
                        if (customerOwnerId != -1 && !customerOwnerId.equals(loggedInUserId))
                            throw new RuntimeException("Not authorized to perform operations on customer with different customer owner id");
                        predicates.add(criteriaBuilder.and(criteriaBuilder.equal(vendor.join("user").get("id"), loggedInUserId)));
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

            final Page<Vendor> vendorList = vendorRepository.findAll(spec, p);

            final List<VendorResponseDto> vendorResponseDtoList = vendorList
                    .getContent()
                    .stream()
                    .map(vendor -> {
                        VendorResponseDto vendorResponseDto = vendorMapper.mapToVendorResponseDto(VendorResponseDto.builder().build(), vendor);
                        if(null != vendor.getUser()) vendorResponseDto.setUserId(vendor.getUser().getId());
                        if (null != vendor.getState()) vendorResponseDto.setStateResponseDto(stateMapper.mapToStateResponseDto(StateResponseDto.builder().build(), vendor.getState()));
                        if (null != vendor.getCountry()) vendorResponseDto.setCountryResponseDto(countryMapper.mapToCountryResponseDto(CountryResponseDto.builder().build(), vendor.getCountry()));
                        if (null != vendor.getRemitState()) vendorResponseDto.setRemitStateResponseDto(stateMapper.mapToStateResponseDto(StateResponseDto.builder().build(), vendor.getRemitState()));
                        if (null != vendor.getRemitCountry()) vendorResponseDto.setRemitCountryResponseDto(countryMapper.mapToCountryResponseDto(CountryResponseDto.builder().build(), vendor.getRemitCountry()));
                        vendorResponseDto.setInventoryItems((null == vendor.getInventoryList()) ? 0 : vendor.getInventoryList().size());
                        return vendorResponseDto;
                    })
                    .collect(Collectors.toList());

            response.setMessage("List of vendors in the database");
            response.setContent(vendorResponseDtoList);
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            logger.error("Error occurred while fetching all the vendors from the database: {}", e.getLocalizedMessage());
            response.setMessage(e.getLocalizedMessage());
            response.setContent(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Saves a vendor based on the provided request DTO.
     *
     * @param requestDto the vendor request DTO
     */
    @Override
    public BasicRestResponse saveVendor(final VendorRequestDto requestDto, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            logger.info("API called to save the vendor in the database");
            final Vendor vendor = Vendor.builder().build();
            performSave(requestDto, vendor, null, request);
            response.setMessage("Vendor Saved Successfully");
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            logger.error("Error occurred while saving the vendor in the database {}", e.getLocalizedMessage());
            response.setMessage("Error occurred while saving the vendor in the database");
            response.setContent(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Deletes a vendor based on the provided ID.
     *
     * @param vendorId the vendor ID
     * @return a message indicating the deletion status
     */
    @Override
    public BasicRestResponse deleteVendor(final Integer vendorId, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            logger.info("API called to delete the vendor from the database");

            final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();
            final Integer loggedInUserID = loggedInUserUtil.getLoggedInUserID();

            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

            final Optional<Vendor> optionalVendor = vendorRepository.findById(vendorId);
            if(optionalVendor.isEmpty()) throw new ResourceNotFoundException(String.format("No vendor found with the given id: %1$s", vendorId));
            final Vendor vendor = optionalVendor.get();

            if(loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
                if(customerOwnerId == -1) throw new RuntimeException("Please select a customer owner");
                else if(!customerOwnerId.equals(vendor.getUser().getId())) throw new RuntimeException("Cannot perform operations on vendor with different customer owner id");
            } else if (loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
                if(customerOwnerId != -1 && !loggedInUserID.equals(customerOwnerId)) throw new RuntimeException("Cannot perform operations on vendor with different customer owner id");
                if (!vendor.getUser().getId().equals(loggedInUserID))
                    throw new RuntimeException("Not authorized to perform operations on vendor with different customer owner Id");
            } else{
                throw new RuntimeException("Not Authorized");
            }

            vendorRepository.deleteById(vendorId);
            final String message = vendorRepository.findById(vendorId).isPresent() ? String.format("Vendor with the id %1$s failed to get deleted", vendorId) : String.format("Vendor with the id %1$s is deleted successfully", vendorId);
            response.setMessage(message);
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            logger.error("Error occurred while deleting the vendor from the database {}", e.getLocalizedMessage());
            response.setMessage(e.getLocalizedMessage());
            response.setContent(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Updates a vendor based on the provided request DTO and vendor ID.
     *
     * @param requestDto the vendor request DTO
     * @param id   the vendor ID
     */
    @Override
    public BasicRestResponse updateVendor(final VendorRequestDto requestDto, final Integer id, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            logger.info("API called to update the vendor in the database");
            if(null == id) {
                throw new RuntimeException("Id cannot be null during update command");
            } else {
                Optional<Vendor> optionalVendor = vendorRepository.findById(id);
                if(optionalVendor.isEmpty()) {
                    throw new ResourceNotFoundException("No vendor exists with the given vendor ID");
                }
                final Vendor vendor = optionalVendor.get();
                performSave(requestDto, vendor, id, request);
            }
            response.setMessage("Vendor updated successfully");
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            logger.error("Error occurred while updating the vendor in the database {}", e.getLocalizedMessage());
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Performs the actual saving of a vendor entity based on the request DTO and vendor object.
     *
     * @param vendorRequestDto the vendor request DTO
     * @param vendor     the vendor object to be saved or updated
     * @param id   the vendor ID (null for new vendors)
     */
    public Vendor performSave(final VendorRequestDto vendorRequestDto, final Vendor vendor, final Integer id, final HttpServletRequest request) {
        final Vendor savedVendor;
        try {
            final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();
            final Integer loggedInUserId = loggedInUserUtil.getLoggedInUserID();
            logger.info("performSave() function called");
            if(null == id) {
                vendor.setCreationDate(new Date(System.currentTimeMillis()));
                vendor.setLastModifiedDate(new Date(System.currentTimeMillis()));
            } else {
                vendor.setLastModifiedDate(new Date(System.currentTimeMillis()));
            }

            Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

            User user = null;
            if (loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
                if(customerOwnerId == -1) throw new RuntimeException("Please select a customer owner");
                Optional<User> optionalUser = userRepository.findById(customerOwnerId);
                if (optionalUser.isEmpty())
                    throw new ResourceNotFoundException(String.format("No user found with the given id: %1$s", customerOwnerId));
                if (!optionalUser.get().getRole().getName().equals(AppConstants.Role.CUSTOMER_OWNER))
                    throw new RuntimeException(String.format("User with the given id: %1$s is not of Customer Owner role.", customerOwnerId));
                user = optionalUser.get();
            } else if (loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
                if (customerOwnerId != -1 && !loggedInUserId.equals(customerOwnerId))
                    throw new RuntimeException("Cannot do operations on vendor with different customer owner Id");
                customerOwnerId = loggedInUserId;
                Optional<User> optionalUser = userRepository.findById(loggedInUserId);
                if (optionalUser.isEmpty())
                    throw new ResourceNotFoundException(String.format("No user found with the given id: %1$s", customerOwnerId));
                user = optionalUser.get();
            } else {
                throw new RuntimeException("Not Authorized");
            }

            vendor.setUser(user);

            vendorMapper.mapToVendor(vendor, vendorRequestDto);

            if (null != vendorRequestDto.getStateId()) {
                final Optional<State> optionalState = stateRepository.findById(vendorRequestDto.getStateId());
                if (optionalState.isEmpty())
                    throw new ResourceNotFoundException(String.format("No state found with the given Id: %1$s", vendorRequestDto.getStateId()));
                vendor.setState(optionalState.get());
            } else {
                if (null == id) throw new RuntimeException("State cannot be null.");
            }

            if (null != vendorRequestDto.getCountryId()) {
                final Optional<Country> optionalCountry = countryRepository.findById(vendorRequestDto.getCountryId());
                if (optionalCountry.isEmpty())
                    throw new ResourceNotFoundException(String.format("No country found with the given Id: %1$s", vendorRequestDto.getCountryId()));
                vendor.setCountry(optionalCountry.get());
            } else {
                if (null == id) throw new RuntimeException("Country cannot be null.");
            }

            if (null != vendorRequestDto.getRemitStateId()) {
                final Optional<State> optionalState = stateRepository.findById(vendorRequestDto.getRemitStateId());
                if (optionalState.isEmpty())
                    throw new ResourceNotFoundException(String.format("No state found with the given Id: %1$s", vendorRequestDto.getRemitStateId()));
                vendor.setRemitState(optionalState.get());
            } else {
                if (null == id) throw new RuntimeException("State cannot be null.");
            }

            if (null != vendorRequestDto.getRemitCountryId()) {
                final Optional<Country> optionalCountry = countryRepository.findById(vendorRequestDto.getRemitCountryId());
                if (optionalCountry.isEmpty())
                    throw new ResourceNotFoundException(String.format("No country found with the given Id: %1$s", vendorRequestDto.getRemitCountryId()));
                vendor.setRemitCountry(optionalCountry.get());
            } else {
                if (null == id) throw new RuntimeException("Country cannot be null.");
            }

            savedVendor = vendorRepository.save(vendor);
        } catch (Exception e) {
            logger.error("Error occurred during performSave() operation {}", e.getLocalizedMessage());
            throw e;
        }
        return savedVendor;
    }
}