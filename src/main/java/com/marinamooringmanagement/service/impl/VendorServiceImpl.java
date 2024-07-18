package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.metadata.CountryMapper;
import com.marinamooringmanagement.mapper.metadata.StateMapper;
import com.marinamooringmanagement.mapper.VendorMapper;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.entity.metadata.Country;
import com.marinamooringmanagement.model.entity.metadata.State;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.VendorRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.metadata.CountryResponseDto;
import com.marinamooringmanagement.model.response.metadata.StateResponseDto;
import com.marinamooringmanagement.model.response.VendorResponseDto;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.repositories.metadata.CountryRepository;
import com.marinamooringmanagement.repositories.metadata.StateRepository;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.VendorService;
import com.marinamooringmanagement.utils.ConversionUtils;
import com.marinamooringmanagement.utils.PhoneNumberUtil;
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

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ConversionUtils conversionUtils;

    @Autowired
    private PhoneNumberUtil phoneNumberUtil;

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

            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

            Specification<Vendor> spec = new Specification<Vendor>() {
                @Override
                public Predicate toPredicate(Root<Vendor> vendor, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if(null != searchText) {
                        String lowerCaseSearchText = "%" + searchText.toLowerCase() + "%";

                        if(conversionUtils.canConvertToInt(lowerCaseSearchText)) {
                            predicates.add(criteriaBuilder.like(vendor.get("id"), searchText));
                        }

                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(vendor.get("companyName")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.lower(vendor.get("website")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.lower(vendor.get("companyPhoneNumber")), lowerCaseSearchText),
                                criteriaBuilder.like(criteriaBuilder.lower(vendor.get("companyEmail")), lowerCaseSearchText)
                        ));
                    }

                    predicates.add(authorizationUtil.fetchPredicate(customerOwnerId, vendor, criteriaBuilder));

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
            response.setTotalSize(vendorRepository.findAll(spec).size());
            response.setCurrentSize(vendorResponseDtoList.size());
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
            response.setMessage(e.getLocalizedMessage());
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

            Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            if(-1 == customerOwnerId && null != request.getAttribute("CUSTOMER_OWNER_ID")) customerOwnerId = (Integer) request.getAttribute("CUSTOMER_OWNER_ID");

            final Optional<Vendor> optionalVendor = vendorRepository.findById(vendorId);

            if(optionalVendor.isEmpty()) throw new ResourceNotFoundException(String.format("No vendor found with the given id: %1$s", vendorId));
            final Vendor vendor = optionalVendor.get();

            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            if(null != vendor.getUser()) {
                if(!vendor.getUser().getId().equals(user.getId())) throw new RuntimeException(String.format("Vendor with the id: %1$s is associated with some other user", vendorId));
            } else {
                throw new RuntimeException(String.format("Vendor with the id: %1$s is not associated with any User", vendorId));
            }

            List<Inventory> inventoryList = vendor.getInventoryList();
            inventoryRepository.deleteAll(inventoryList);

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

    @Override
    public BasicRestResponse fetchVendorById(final Integer vendorId, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

            Optional<Vendor> optionalVendor = vendorRepository.findById(vendorId);
            if(optionalVendor.isEmpty()) throw new RuntimeException(String.format("No vendor exist with the given id: %1$s", vendorId));

            User user = authorizationUtil.checkAuthority(customerOwnerId);

            final Vendor vendor = optionalVendor.get();

            if(null == vendor.getUser()) throw new RuntimeException(String.format("Vendor with the given id: %1$s is associated with no user", vendorId));

            if(!vendor.getUser().getId().equals(user.getId())) throw new RuntimeException(String.format("Vendor with the given id: %1$s is associated with other customer owner", vendorId));

            final VendorResponseDto vendorResponseDto = vendorMapper.mapToVendorResponseDto(VendorResponseDto.builder().build(), vendor);

            if(null != vendor.getUser()) vendorResponseDto.setUserId(vendor.getUser().getId());
            if (null != vendor.getState()) vendorResponseDto.setStateResponseDto(stateMapper.mapToStateResponseDto(StateResponseDto.builder().build(), vendor.getState()));
            if (null != vendor.getCountry()) vendorResponseDto.setCountryResponseDto(countryMapper.mapToCountryResponseDto(CountryResponseDto.builder().build(), vendor.getCountry()));
            if (null != vendor.getRemitState()) vendorResponseDto.setRemitStateResponseDto(stateMapper.mapToStateResponseDto(StateResponseDto.builder().build(), vendor.getRemitState()));
            if (null != vendor.getRemitCountry()) vendorResponseDto.setRemitCountryResponseDto(countryMapper.mapToCountryResponseDto(CountryResponseDto.builder().build(), vendor.getRemitCountry()));

            response.setContent(vendorResponseDto);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage(String.format("Vendor with the given id: %1$s fetched successfully", vendorId));

        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getLocalizedMessage());
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
            logger.info("performSave() function called");
            if(null == id) {
                vendor.setCreationDate(new Date(System.currentTimeMillis()));
                vendor.setLastModifiedDate(new Date(System.currentTimeMillis()));
            } else {
                vendor.setLastModifiedDate(new Date(System.currentTimeMillis()));
            }

            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            if(null != vendor.getUser() && !vendor.getUser().getId().equals(user.getId())) throw new RuntimeException(String.format("Vendor with the id: %1$s is associated with some other customer owner", id));

            vendor.setUser(user);

            vendorMapper.mapToVendor(vendor, vendorRequestDto);

            if(null != vendor.getCompanyEmail()) {
                Optional<Vendor> optionalVendor = vendorRepository.findByCompanyEmail(vendor.getCompanyEmail());
                if(null == id) {
                    if(optionalVendor.isPresent()) throw new RuntimeException(String.format("Given company email: %1$s is already present", vendor.getCompanyEmail()));
                } else {
                    if(optionalVendor.isPresent() && !optionalVendor.get().getId().equals(id)) throw new RuntimeException(String.format("Given company email: %1$s is already present", vendor.getCompanyEmail()));
                }
            }

            if(null != vendor.getRemitEmailAddress()) {
                Optional<Vendor> optionalVendor = vendorRepository.findByRemitEmailAddress(vendor.getRemitEmailAddress());
                if(null == id) {
                    if(optionalVendor.isPresent()) throw new RuntimeException(String.format("Given remit email: %1$s is already present", vendor.getRemitEmailAddress()));
                } else {
                    if(optionalVendor.isPresent() && !optionalVendor.get().getId().equals(id)) throw new RuntimeException(String.format("Given remit email: %1$s is already present", vendor.getRemitEmailAddress()));
                }
            }

            if(null != vendor.getSalesRepEmail()) {
                Optional<Vendor> optionalVendor = vendorRepository.findBySalesRepEmail(vendor.getSalesRepEmail());
                if(null == id) {
                    if(optionalVendor.isPresent()) throw new RuntimeException(String.format("Given sales representative email: %1$s is already present", vendor.getSalesRepEmail()));
                } else {
                    if(optionalVendor.isPresent() && !optionalVendor.get().getId().equals(id)) throw new RuntimeException(String.format("Given sales representative email: %1$s is already present", vendor.getSalesRepEmail()));
                }
            }

            if(null != vendorRequestDto.getCompanyPhoneNumber()) {
                String phoneNumber = phoneNumberUtil.validateAndConvertToStandardFormatPhoneNumber(vendorRequestDto.getCompanyPhoneNumber());

                Optional<Vendor> optionalVendor = vendorRepository.findByCompanyPhoneNumber(phoneNumber);
                if(optionalVendor.isPresent()) {
                    if(null == vendor.getId() || (null != optionalVendor.get().getId() && !optionalVendor.get().getId().equals(vendor.getId()))) throw new RuntimeException(String.format("This phone number is already in use. Please enter a different phone number."));
                }

                vendor.setCompanyPhoneNumber(phoneNumber);

            } else {
                if(null == id) throw new RuntimeException(String.format("Company Phone cannot be blank during save"));
            }

            if(null != vendorRequestDto.getSalesRepPhoneNumber()) {
                String phoneNumber = phoneNumberUtil.validateAndConvertToStandardFormatPhoneNumber(vendorRequestDto.getSalesRepPhoneNumber());

                Optional<Vendor> optionalVendor = vendorRepository.findBySalesRepPhoneNumber(phoneNumber);
                if(optionalVendor.isPresent()) {
                    if(null == vendor.getId() || (null != optionalVendor.get().getId() && !optionalVendor.get().getId().equals(vendor.getId()))) throw new RuntimeException(String.format("This phone number is already in use. Please enter a different phone number."));
                }

                vendor.setSalesRepPhoneNumber(phoneNumber);

            } else {
                if(null == id) throw new RuntimeException(String.format("Sales representation Phone cannot be blank during save"));
            }

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