package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.BoatyardMapper;
import com.marinamooringmanagement.mapper.CountryMapper;
import com.marinamooringmanagement.mapper.MooringMapper;
import com.marinamooringmanagement.mapper.StateMapper;
import com.marinamooringmanagement.model.dto.BoatyardDto;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.BoatyardRequestDto;
import com.marinamooringmanagement.model.response.*;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.security.config.LoggedInUserUtil;
import com.marinamooringmanagement.service.BoatyardService;
import com.marinamooringmanagement.service.MooringService;
import com.marinamooringmanagement.utils.SortUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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
 * Implementation of the BoatYardService interface.
 * Provides methods for CRUD operations on BoatYard entities.
 */
@Service
public class BoatyardServiceImpl implements BoatyardService {

    @Autowired
    private BoatyardRepository boatYardRepository;

    @Autowired
    private BoatyardMapper boatyardMapper;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private MooringMapper mooringMapper;

    @Autowired
    private MooringRepository mooringRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StateMapper stateMapper;

    @Autowired
    private CountryMapper countryMapper;

    @Autowired
    private SortUtils sortUtils;

    @Autowired
    private MooringService mooringService;

    @Autowired
    private LoggedInUserUtil loggedInUserUtil;

    @Autowired
    private UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(BoatyardServiceImpl.class);

    /**
     * Saves a BoatYard entity.
     *
     * @param boatYardRequestDto The BoatYardDto containing the data to be saved.
     */
    @Override
    public BasicRestResponse saveBoatyard(final BoatyardRequestDto boatYardRequestDto, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Boatyard boatyard = new Boatyard();
            performSave(boatYardRequestDto, boatyard, null, request);
            response.setMessage("Boatyard Saved Successfully");
            response.setStatus(HttpStatus.CREATED.value());
            log.info(String.format("Saving data in the database for Boatyard ID %d", boatYardRequestDto.getId()));

        } catch (Exception e) {
            log.error(String.format("Exception occurred while performing save operation: %s", e.getMessage()), e);

            throw new DBOperationException(e.getMessage(), e);
        }
        return response;
    }

    /**
     * Fetches a list of boatyards based on the provided search request parameters and search text.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @param searchText        the text used to search for specific boatyards by name, location, or other relevant criteria.
     * @return a BasicRestResponse containing the results of the boatyard search.
     */
    @Override
    public BasicRestResponse fetchBoatyards(final BaseSearchRequest baseSearchRequest, final String searchText, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();
            final Integer loggedInUserId = loggedInUserUtil.getLoggedInUserID();

            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

            Specification<Boatyard> spec = new Specification<Boatyard>() {
                @Override
                public Predicate toPredicate(Root<Boatyard> boatyard, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if (null != searchText) {
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(boatyard.get("boatyardName"), "%" + searchText + "%"),
                                criteriaBuilder.like(boatyard.get("street"), "%" + searchText + "%")
                        ));
                    }

                    if (loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
                        if(customerOwnerId == -1) throw new RuntimeException("Please select a customer owner");
                        predicates.add(criteriaBuilder.and(criteriaBuilder.equal(boatyard.join("user").get("id"), customerOwnerId)));
                    } else if (loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
                        if (customerOwnerId != -1 && !customerOwnerId.equals(loggedInUserId))
                            throw new RuntimeException("Not authorized to perform operations on boatyards with different customer owner id");
                        predicates.add(criteriaBuilder.and(criteriaBuilder.equal(boatyard.join("user").get("id"), loggedInUserId)));
                    } else {
                        throw new RuntimeException("Not Authorized");
                    }

                    return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };

            final Sort sort = sortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir());
            final Pageable p = PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize(), sort);

            Page<Boatyard> boatyardList = boatYardRepository.findAll(spec, p);

            final List<BoatyardResponseDto> boatyardDtoList = boatyardList
                    .getContent()
                    .stream()
                    .map(boatyard -> {
                        BoatyardResponseDto boatyardResponseDto = BoatyardResponseDto.builder().build();
                        boatyardMapper.mapToBoatYardResponseDto(boatyardResponseDto, boatyard);
                        if (null != boatyard.getState())
                            boatyardResponseDto.setStateResponseDto(stateMapper.mapToStateResponseDto(StateResponseDto.builder().build(), boatyard.getState()));
                        if (null != boatyard.getCountry())
                            boatyardResponseDto.setCountryResponseDto(countryMapper.mapToCountryResponseDto(CountryResponseDto.builder().build(), boatyard.getCountry()));
                        boatyardResponseDto.setMooringInventoried(boatyard.getMooringList().size());
                        boatyardResponseDto.setUserId(boatyard.getUser().getId());
                        return boatyardResponseDto;
                    })
                    .collect(Collectors.toList());

            response.setContent(boatyardDtoList);
            response.setMessage("All boatyard are fetched successfully");
            response.setStatus(HttpStatus.OK.value());
            log.info(String.format("BoatYard fetched successfully"));
        } catch (Exception e) {
            log.error(String.format("Error occurred while fetching BoatYard: %s", e.getMessage()), e);
            throw e;
        }
        return response;
    }

    /**
     * Retrieves a BoatYard entity by its ID.
     *
     * @param id The ID of the BoatYard to retrieve.
     * @return The BoatYardDto object corresponding to the given ID.
     */
    @Override
    public BoatyardDto getbyId(final Integer id) {
        if (id == null) {
            throw new RuntimeException("ID cannot be null");
        }
        try {
            Optional<Boatyard> BoatYardEntityOptional = boatYardRepository.findById(id);
            if (BoatYardEntityOptional.isPresent()) {
                return boatyardMapper.toDto(BoatYardEntityOptional.get());
            }
            throw new DBOperationException(String.format("BoatYard with ID : %d doesn't exist", id));
        } catch (Exception e) {
            log.error(String.format("Error occurred while retrieving BoatYard for ID: %d: %s", id, e.getMessage()), e);

            throw new DBOperationException(String.format("BoatYard with ID : %d doesn't exist", id));
        }
    }


    /**
     * Deletes a BoatYard entity by its ID.
     *
     * @param id The ID of the BoatYard to delete.
     */
    @Override
    public BasicRestResponse deleteBoatyardById(final Integer id, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();
            final Integer loggedInUserID = loggedInUserUtil.getLoggedInUserID();

            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

            Optional<Boatyard> optionalBoatyard = boatYardRepository.findById(id);

            if (optionalBoatyard.isEmpty())
                throw new DBOperationException(String.format("No boatyard found with the given id: %1$s", id));

            if (null == optionalBoatyard.get().getBoatyardName())
                throw new RuntimeException(String.format("Boatyard Name is not found in the boatyard entity with the id as %1$s", id));

            if(loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
                if(customerOwnerId == -1) throw new RuntimeException("Please select a customer owner");
                else if(!customerOwnerId.equals(optionalBoatyard.get().getUser().getId())) throw new RuntimeException("Cannot perform operations on customer with different customer owner id");
            } else if (loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
                if(customerOwnerId != -1 && !loggedInUserID.equals(customerOwnerId)) throw new RuntimeException("Cannot perform operations on customer with different customer owner id");
                if (!optionalBoatyard.get().getUser().getId().equals(loggedInUserID))
                    throw new RuntimeException("Not authorized to perform operations on mooring with different customer owner Id");
            } else{
                throw new RuntimeException("Not Authorized");
            }

            List<Mooring> mooringList = optionalBoatyard.get().getMooringList();

            mooringList.stream().map(mooring -> mooringService.deleteMooring(mooring.getId(), request));

            boatYardRepository.deleteById(id);

            response.setMessage(String.format("BoatYard with ID %d deleted successfully", id));
            response.setStatus(HttpStatus.OK.value());
            log.info(String.format("BoatYard with ID %d deleted successfully", id));
        } catch (Exception e) {
            log.error(String.format("Error occurred while deleting BoatYard with ID %d", id));
            throw new DBOperationException(e.getMessage(), e);
        }
        return response;
    }

    /**
     * Updates a BoatYard entity.
     *
     * @param boatYardRequestDto The BoatYardDto containing the updated data.
     * @param id                 The ID of the BoatYard to update.
     * @return A BasicRestResponse indicating the status of the operation.
     */
    @Override
    public BasicRestResponse updateBoatyard(final BoatyardRequestDto boatYardRequestDto, final Integer id, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        try {
            if (null == id) {
                log.info(String.format("Update attempt without a BoatYard ID provided in the request DTO"));

                throw new ResourceNotFoundException(String.format("BoatYard not found with id: %1$s", id));
            }
            Optional<Boatyard> optionalBoatYard = boatYardRepository.findById(id);
            if (optionalBoatYard.isPresent()) {
                Boatyard boatyard = optionalBoatYard.get();
                performSave(boatYardRequestDto, boatyard, id, request);
                response.setMessage(String.format("BoatYard with the given boatyard id %d updated successfully!!!", id));
                response.setStatus(HttpStatus.OK.value());
            } else {
                throw new ResourceNotFoundException(String.format("BoatYard with Id: %d not found", id));
            }
        } catch (Exception e) {
            log.error(String.format("Error occurred while updating boatyard: %s", e.getMessage()), e);

            throw new DBOperationException(e.getMessage(), e);
        }
        return response;
    }

    /**
     * Fetches Moorings related to a specific boatyard from the database.
     *
     * @param id the ID of the boatyard.
     * @return a {@link BasicRestResponse} containing the moorings related to the boatyard.
     */
    @Override
    public BasicRestResponse fetchMooringsWithBoatyard(final Integer id) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        try {
            final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();

            Optional<Boatyard> optionalBoatyard = boatYardRepository.findById(id);
            if (optionalBoatyard.isEmpty())
                throw new ResourceNotFoundException(String.format("No boatyard found with the given id: %1$s", id));

            if (loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
                if (!optionalBoatyard.get().getUser().getId().equals(loggedInUserUtil.getLoggedInUserID()))
                    throw new RuntimeException("Not authorized to perform operations on boatyard with different customer owner Id");
            } else if (loggedInUserRole.equals(AppConstants.Role.FINANCE) || loggedInUserRole.equals(AppConstants.Role.TECHNICIAN)) {
                throw new RuntimeException("Not Authorized");
            }

            List<MooringResponseDto> mooringResponseDtoList = optionalBoatyard
                    .get()
                    .getMooringList()
                    .stream()
                    .map(mooring -> {
                        MooringResponseDto mooringResponseDto = mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), mooring);
                        if (null != mooring.getCustomer())
                            mooringResponseDto.setCustomerId(mooring.getCustomer().getId());
                        if (null != mooring.getUser()) mooringResponseDto.setUserId(mooring.getUser().getId());
                        return mooringResponseDto;
                    })
                    .collect(Collectors.toList());

            log.info(String.format("Moorings fetched with the boatyard id as %1$s", id));
            response.setMessage(String.format("Moorings fetched with the boatyard id as %1$s", id));
            response.setTime(new Timestamp(System.currentTimeMillis()));
            response.setContent(mooringResponseDtoList);
            response.setStatus(HttpStatus.OK.value());

        } catch (Exception e) {
            response.setMessage(String.format(e.getLocalizedMessage()));
            response.setTime(new Timestamp(System.currentTimeMillis()));
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Performs the saving operation for a Boatyard entity.
     *
     * @param boatyardRequestDto The BoatyardRequestDto containing the data to be saved.
     * @param boatyard           The Boatyard entity to be saved or updated.
     * @param id                 The ID of the Boatyard entity, if available.
     * @throws DBOperationException If an error occurs during the saving operation.
     */
    public void performSave(final BoatyardRequestDto boatyardRequestDto, final Boatyard boatyard, Integer id, final HttpServletRequest request) {
        try {
            final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();
            final Integer loggedInUserId = loggedInUserUtil.getLoggedInUserID();

            boatyard.setLastModifiedDate(new Date(System.currentTimeMillis()));

            Optional<Boatyard> optionalBoatyard = null;

            if (null != boatyardRequestDto.getBoatyardId()) {
                optionalBoatyard = boatYardRepository.findByBoatyardId(boatyardRequestDto.getBoatyardId());
                if (optionalBoatyard.isPresent()) {
                    if (null == id) {
                        throw new RuntimeException(String.format("Given Boatyard Id: %1$s is already present", boatyardRequestDto.getBoatyardId()));
                    } else {
                        if (!optionalBoatyard.get().getId().equals(id))
                            throw new RuntimeException(String.format("Given Boatyard Id: %1$s is already present", boatyardRequestDto.getBoatyardId()));
                    }
                }
            }

            if (null != boatyardRequestDto.getEmailAddress()) {
                optionalBoatyard = boatYardRepository.findByEmailAddress(boatyardRequestDto.getEmailAddress());
                if (optionalBoatyard.isPresent()) {
                    if (null == id) {
                        throw new RuntimeException(String.format("Given email address: %1$s is already present", boatyardRequestDto.getEmailAddress()));
                    } else {
                        if (!optionalBoatyard.get().getId().equals(id))
                            throw new RuntimeException(String.format("Given email address: %1$s is already present", boatyardRequestDto.getEmailAddress()));
                    }
                }
            }

            if (null != boatyardRequestDto.getBoatyardName()) {
                optionalBoatyard = boatYardRepository.findByBoatyardName(boatyardRequestDto.getBoatyardName());
                if (optionalBoatyard.isPresent()) {
                    if (null == id) {
                        throw new RuntimeException(String.format("Given boatyard name: %1$s is already present", boatyardRequestDto.getBoatyardName()));
                    } else {
                        if (!optionalBoatyard.get().getId().equals(id))
                            throw new RuntimeException(String.format("Given boatyard name: %1$s is already present", boatyardRequestDto.getBoatyardName()));
                    }
                }
            }

            Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

            User user = null;
            if (loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
                Optional<User> optionalUser = userRepository.findById(customerOwnerId);
                if (optionalUser.isEmpty())
                    throw new ResourceNotFoundException(String.format("No user found with the given id: %1$s", customerOwnerId));
                if (!optionalUser.get().getRole().getName().equals(AppConstants.Role.CUSTOMER_OWNER))
                    throw new RuntimeException(String.format("User with the given id: %1$s is not of Customer Owner role.", customerOwnerId));
                user = optionalUser.get();
            } else if (loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
                if (customerOwnerId != -1 && !loggedInUserId.equals(customerOwnerId))
                    throw new RuntimeException("Cannot do operations on customer with different customer owner Id");
                customerOwnerId = loggedInUserId;
                Optional<User> optionalUser = userRepository.findById(loggedInUserId);
                if (optionalUser.isEmpty())
                    throw new ResourceNotFoundException(String.format("No user found with the given id: %1$s", customerOwnerId));
                user = optionalUser.get();
            } else {
                throw new RuntimeException("Not Authorized");
            }

            boatyard.setUser(user);

            boatyardMapper.mapToBoatYard(boatyard, boatyardRequestDto);

            if (null == id) boatyard.setCreationDate(new Date());
            boatyard.setLastModifiedDate(new Date());

            if (null != boatyardRequestDto.getStateId()) {
                final Optional<State> optionalState = stateRepository.findById(boatyardRequestDto.getStateId());
                if (optionalState.isEmpty())
                    throw new ResourceNotFoundException(String.format("No state found with the given Id: %1$s", boatyardRequestDto.getStateId()));
                boatyard.setState(optionalState.get());
            } else {
                if (null == id) throw new RuntimeException("State cannot be null.");
            }

            if (null != boatyardRequestDto.getCountryId()) {
                final Optional<Country> optionalCountry = countryRepository.findById(boatyardRequestDto.getCountryId());
                if (optionalCountry.isEmpty())
                    throw new ResourceNotFoundException(String.format("No country found with the given Id: %1$s", boatyardRequestDto.getCountryId()));
                boatyard.setCountry(optionalCountry.get());
            } else {
                if (null == id) throw new RuntimeException("Country cannot be null.");
            }

            final Boatyard savedBoatyard = boatYardRepository.save(boatyard);

            if (StringUtils.isNotEmpty(boatyardRequestDto.getBoatyardName())
                    && StringUtils.isNotEmpty(boatyard.getBoatyardName())
                    && !StringUtils.equals(boatyardRequestDto.getBoatyardName(), boatyard.getBoatyardName())) {

                List<Mooring> mooringList = boatyard.getMooringList().stream().map(mooring -> {
                    mooring.setBoatyard(savedBoatyard);
                    return mooring;
                }).toList();

                mooringRepository.saveAll(mooringList);
            }

            log.info(String.format("Boatyard saved successfully with ID: %d", boatyard.getId()));
        } catch (Exception e) {
            log.error(String.format("Error occurred during performSave() function: %s", e.getMessage()), e);
            throw new DBOperationException(e.getMessage(), e);
        }
    }
}