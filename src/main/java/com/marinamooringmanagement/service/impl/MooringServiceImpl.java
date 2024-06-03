package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.BoatyardMapper;
import com.marinamooringmanagement.mapper.CustomerMapper;
import com.marinamooringmanagement.mapper.MooringMapper;
import com.marinamooringmanagement.mapper.MooringStatusMapper;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.BoatyardResponseDto;
import com.marinamooringmanagement.model.response.MooringResponseDto;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.model.request.MooringRequestDto;
import com.marinamooringmanagement.security.config.LoggedInUserUtil;
import com.marinamooringmanagement.service.MooringService;
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

import java.sql.Timestamp;
import java.util.*;

import java.util.stream.Collectors;

/**
 * Service implementation for managing Mooring entities.
 */
@Service
public class MooringServiceImpl implements MooringService {

    private static final Logger log = LoggerFactory.getLogger(MooringServiceImpl.class);

    @Autowired
    private MooringMapper mooringMapper;

    @Autowired
    private MooringRepository mooringRepository;

    @Autowired
    private BoatyardRepository boatyardRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private SortUtils sortUtils;

    @Autowired
    private MooringStatusMapper mooringStatusMapper;

    @Autowired
    private MooringStatusRepository mooringStatusRepository;

    @Autowired
    private BoatTypeRepository boatTypeRepository;

    @Autowired
    private SizeOfWeightRepository sizeOfWeightRepository;

    @Autowired
    private TypeOfWeightRepository typeOfWeightRepository;

    @Autowired
    private TopChainConditionRepository topChainConditionRepository;

    @Autowired
    private EyeConditionRepository eyeConditionRepository;

    @Autowired
    private BottomChainConditionRepository bottomChainConditionRepository;

    @Autowired
    private ShackleSwivelConditionRepository shackleSwivelConditionRepository;

    @Autowired
    private PennantConditionRepository pennantConditionRepository;

    @Autowired
    private LoggedInUserUtil loggedInUserUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoatyardMapper boatyardMapper;

    /**
     * Fetches a list of moorings based on the provided search request parameters and search text.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @param searchText        the text used to search for specific moorings by name, location, or other relevant criteria.
     * @return a BasicRestResponse containing the results of the mooring search.
     */
    @Override
    public BasicRestResponse fetchMoorings(final BaseSearchRequest baseSearchRequest, final String searchText, final Integer customerOwnerId) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to fetch all the moorings in the database");

            final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();
            final Integer loggedInUserId = loggedInUserUtil.getLoggedInUserID();

            Specification<Mooring> spec = new Specification<Mooring>() {
                @Override
                public Predicate toPredicate(Root<Mooring> mooring, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if (null != searchText) {
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(mooring.get("boatName"), "%" + searchText + "%"),
                                criteriaBuilder.like(mooring.get("mooringId"), "%" + searchText + "%")
                        ));
                    }

                    if(loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
                        if(null == customerOwnerId) throw new RuntimeException("Please select a customer owner");
                        predicates.add(criteriaBuilder.and(criteriaBuilder.equal(mooring.join("user").get("id"), customerOwnerId)));
                    } else if(loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
                        if(null != customerOwnerId && !customerOwnerId.equals(loggedInUserId)) throw new RuntimeException("Not authorized to perform operations on boatyards with different customer owner id");
                        predicates.add(criteriaBuilder.and(criteriaBuilder.equal(mooring.join("user").get("id"), loggedInUserId)));
                    } else {
                        throw new RuntimeException("Not Authorized");
                    }

                    return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };

            final Pageable pageable = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    sortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir()));

            final Page<Mooring> mooringList = mooringRepository.findAll(spec, pageable);

            final List<MooringResponseDto> mooringResponseDtoList = mooringList
                    .getContent()
                    .stream()
                    .map(mooring -> {
                        MooringResponseDto mooringResponseDto = mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), mooring);
                        if (null != mooring.getCustomer()) mooringResponseDto.setCustomerId(mooring.getCustomer().getId());
                        if(null != mooring.getUser()) mooringResponseDto.setUserId(mooring.getUser().getId());
                        if(null != mooring.getBoatyard()) mooringResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), mooring.getBoatyard()));
                        return mooringResponseDto;
                    })
                    .collect(Collectors.toList());

            response.setMessage("All moorings fetched successfully.");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(mooringResponseDtoList);
        } catch (Exception e) {
            log.error("Error occurred while fetching all the moorings in the database", e);
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Saves a mooring based on the provided request DTO.
     *
     * @param mooringRequestDto the mooring request DTO
     */
    @Override
    public BasicRestResponse saveMooring(final MooringRequestDto mooringRequestDto) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to save the mooring in the database");
            final Mooring mooring = new Mooring();

            if(null == mooringRequestDto.getMooringId()) throw new RuntimeException("Mooring Id cannot be blank");

            performSave(mooringRequestDto, mooring, null);
            response.setMessage("Mooring saved successfully.");
            response.setStatus(HttpStatus.CREATED.value());
            mooringMapper.mapToMooring(mooring, mooringRequestDto);
        } catch (Exception e) {
            log.error("Error occurred while saving the mooring in the database {}", e.getLocalizedMessage());
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Updates a mooring based on the provided request DTO and mooring ID.
     *
     * @param mooringRequestDto the mooring request DTO
     * @param mooringId         the mooring ID
     */
    @Override
    public BasicRestResponse updateMooring(final MooringRequestDto mooringRequestDto, final Integer mooringId) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to update the mooring with the given mooring ID");
            if (mooringId == null) {
                throw new IllegalArgumentException("Mooring Id not provided for update request");
            }
            Optional<Mooring> optionalMooring = mooringRepository.findById(mooringId);
            final Mooring mooring = optionalMooring.orElseThrow(() -> new ResourceNotFoundException(String.format("Mooring not found with id: %1$s", mooringId)));
            performSave(mooringRequestDto, mooring, mooringId);
            response.setMessage("Mooring updated successfully");
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("Error occurred while updating mooring {}", e.getLocalizedMessage());
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Deletes a mooring based on the provided ID.
     *
     * @param id the mooring ID
     * @return a message indicating the deletion status
     */
    @Override
    public BasicRestResponse deleteMooring(Integer id) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();
            final Integer loggedInUserID = loggedInUserUtil.getLoggedInUserID();

            Optional<Mooring> optionalMooring = mooringRepository.findById(id);
            if (optionalMooring.isEmpty()) throw new RuntimeException(String.format("No mooring exists with %1$s", id));

            if(loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
                if(!optionalMooring.get().getUser().getId().equals(loggedInUserID)) throw new RuntimeException("Not authorized to perform operations on mooring with different customer owner Id");
            } else if(loggedInUserRole.equals(AppConstants.Role.FINANCE) || loggedInUserRole.equals(AppConstants.Role.TECHNICIAN)){
                throw new RuntimeException("Not Authorized");
            }

            if(null != optionalMooring.get().getCustomer()) {
                Customer customer = optionalMooring.get().getCustomer();
                List<Mooring> mooringList = customer.getMooringList();
                mooringList.remove(optionalMooring.get());
                customerRepository.save(customer);
            }

            mooringRepository.deleteById(id);
            Optional<Mooring> optionalMooringAfterDeleteOperation = mooringRepository.findById(id);
            final String message = optionalMooringAfterDeleteOperation.isEmpty() ? String.format("Mooring with id %1$s deleted successfully", id) : String.format("Failed to delete mooring with the given id %1$s", id);
            response.setMessage(message);
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("Error occurred while deleting mooring with id " + id, e);
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Performs the actual saving of a mooring entity based on the request DTO and mooring object.
     *
     * @param mooringRequestDto the mooring request DTO
     * @param mooring           the mooring object to be saved or updated
     * @param id                the mooring ID (null for new moorings)
     */
    public Mooring performSave(final MooringRequestDto mooringRequestDto, final Mooring mooring, final Integer id) {

        try {
            log.info("performSave() function called");

            final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();
            final Integer loggedInUserId = loggedInUserUtil.getLoggedInUserID();

            User user = null;
            if(loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
                if(null == mooringRequestDto.getCustomerOwnerId()) throw new RuntimeException("Please select a customer owner");
                Optional<User> optionalUser = userRepository.findById(mooringRequestDto.getCustomerOwnerId());
                if(optionalUser.isEmpty()) throw new ResourceNotFoundException(String.format("No user found with the given id: %1$s", mooringRequestDto.getCustomerOwnerId()));
                if(!optionalUser.get().getRole().getName().equals(AppConstants.Role.CUSTOMER_OWNER)) throw new RuntimeException(String.format("User with the given id: %1$s is not of Customer Owner role.", mooringRequestDto.getCustomerOwnerId()));
                user = optionalUser.get();
            } else if(loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)){
                if(null != mooringRequestDto.getCustomerOwnerId() && !loggedInUserId.equals(mooringRequestDto.getCustomerOwnerId())) throw new RuntimeException("Cannot do operations on mooring with different customer owner Id");
                Optional<User> optionalUser = userRepository.findById(loggedInUserId);
                if(optionalUser.isEmpty()) throw new ResourceNotFoundException(String.format("No user found with the given id: %1$s", mooringRequestDto.getCustomerOwnerId()));
                user = optionalUser.get();
            } else {
                throw new RuntimeException("Not Authorized");
            }

            Optional<Mooring> optionalMooring = Optional.empty();
            Mooring savedMooring = null;
            mooringMapper.mapToMooring(mooring, mooringRequestDto);
            mooring.setUser(user);

            if(null != mooringRequestDto.getMooringId()) {
                optionalMooring = mooringRepository.findByMooringId(mooringRequestDto.getMooringId());
                if(optionalMooring.isPresent()) {
                    if(null == id) {
                        throw new RuntimeException(String.format("Given mooring Id: %1$s is already present", mooringRequestDto.getMooringId()));
                    } else {
                        if(!optionalMooring.get().getMooringId().equals(mooringRequestDto.getMooringId())) throw new RuntimeException(String.format("Given mooring Id: %1$s is associated with other mooring", mooringRequestDto.getMooringId()));
                    }
                }
            }

            if(null == mooringRequestDto.getCustomerId()) throw new RuntimeException("Customer Id cannot be null");
            Optional<Customer> optionalCustomer = customerRepository.findById(mooringRequestDto.getCustomerId());
            if(optionalCustomer.isEmpty()) throw new RuntimeException(String.format("No customer found with the given customer Id: %1$s", mooringRequestDto.getCustomerId()));
            if(!mooringRequestDto.getCustomerOwnerId().equals(optionalCustomer.get().getUser().getId())) throw new RuntimeException("Customer Owner Id differ in Mooring and Customer");

            if (null == mooringRequestDto.getBoatyardId()) throw new RuntimeException("Boatyard Id cannot be null");
            Optional<Boatyard> optionalBoatyard = boatyardRepository.findById(mooringRequestDto.getBoatyardId());
            if (optionalBoatyard.isEmpty()) throw new ResourceNotFoundException(String.format("No boatyard found with the given boatyard id: %1$s", mooringRequestDto.getBoatyardId()));
            if(!mooringRequestDto.getCustomerOwnerId().equals(optionalBoatyard.get().getUser().getId())) throw new RuntimeException("Customer Owner Id differ in Mooring and Boatyard");
            mooring.setBoatyard(optionalBoatyard.get());

            Optional<MooringStatus> optionalMooringStatus = mooringStatusRepository.findById(1);
            if (optionalMooringStatus.isEmpty())
                throw new ResourceNotFoundException(String.format("No status found with the given id: %1$s", mooringRequestDto.getStatusId()));

            mooring.setMooringStatus(optionalMooringStatus.get());

            if (null != mooringRequestDto.getBoatTypeId()) {
                Optional<BoatType> optionalBoatType = boatTypeRepository.findById(mooringRequestDto.getBoatTypeId());
                if (optionalBoatType.isEmpty())
                    throw new ResourceNotFoundException(String.format("No boat type found with the given id: %1$s", mooringRequestDto.getBoatTypeId()));
                mooring.setBoatType(optionalBoatType.get());
            } else {
                throw new RuntimeException("Boat type cannot be null");
            }

            if (null != mooringRequestDto.getSizeOfWeightId()) {
                Optional<SizeOfWeight> optionalSizeOfWeight = sizeOfWeightRepository.findById(mooringRequestDto.getSizeOfWeightId());
                if (optionalSizeOfWeight.isEmpty())
                    throw new ResourceNotFoundException(String.format("No Size of weight found with the given id: %1$s", mooringRequestDto.getSizeOfWeightId()));
                mooring.setSizeOfWeight(optionalSizeOfWeight.get());
            } else {
                throw new RuntimeException("Size of weight cannot be null");
            }

            if (null != mooringRequestDto.getTypeOfWeightId()) {
                Optional<TypeOfWeight> optionalTypeOfWeight = typeOfWeightRepository.findById(mooringRequestDto.getTypeOfWeightId());
                if (optionalTypeOfWeight.isEmpty())
                    throw new ResourceNotFoundException(String.format("No Type of weight found with the given id: %1$s", mooringRequestDto.getTypeOfWeightId()));
                mooring.setTypeOfWeight(optionalTypeOfWeight.get());
            } else {
                throw new RuntimeException("Type of weight cannot be null");
            }

            if (null != mooringRequestDto.getEyeConditionId()) {
                Optional<EyeCondition> optionalEyeCondition = eyeConditionRepository.findById(mooringRequestDto.getEyeConditionId());
                if (optionalEyeCondition.isEmpty())
                    throw new ResourceNotFoundException(String.format("No Eye condition found with the given id: %1$s", mooringRequestDto.getEyeConditionId()));
                mooring.setEyeCondition(optionalEyeCondition.get());
            } else {
                throw new RuntimeException("Eye condition cannot be null");
            }

            if (null != mooringRequestDto.getTopChainConditionId()) {
                Optional<TopChainCondition> optionalTopChainCondition = topChainConditionRepository.findById(mooringRequestDto.getTopChainConditionId());
                if (optionalTopChainCondition.isEmpty())
                    throw new ResourceNotFoundException(String.format("No Top chain condition found with the given id: %1$s", mooringRequestDto.getTopChainConditionId()));
                mooring.setTopChainCondition(optionalTopChainCondition.get());
            } else {
                throw new RuntimeException("Top chain condition cannot be null");
            }

            if (null != mooringRequestDto.getBottomChainConditionId()) {
                Optional<BottomChainCondition> optionalBottomChainCondition = bottomChainConditionRepository.findById(mooringRequestDto.getBottomChainConditionId());
                if (optionalBottomChainCondition.isEmpty())
                    throw new ResourceNotFoundException(String.format("No Bottom chain condition found with the given id: %1$s", mooringRequestDto.getBottomChainConditionId()));
                mooring.setBottomChainCondition(optionalBottomChainCondition.get());
            } else {
                throw new RuntimeException("Bottom chain condition cannot be null");
            }

            if (null != mooringRequestDto.getShackleSwivelConditionId()) {
                Optional<ShackleSwivelCondition> optionalShackleSwivelCondition = shackleSwivelConditionRepository.findById(mooringRequestDto.getShackleSwivelConditionId());
                if (optionalShackleSwivelCondition.isEmpty())
                    throw new ResourceNotFoundException(String.format("No Shackle swivel condition found with the given id: %1$s", mooringRequestDto.getShackleSwivelConditionId()));
                mooring.setShackleSwivelCondition(optionalShackleSwivelCondition.get());
            } else {
                throw new RuntimeException("Shackle swivel condition cannot be null");
            }

            if (null != mooringRequestDto.getPennantConditionId()) {
                Optional<PennantCondition> optionalPennantCondition = pennantConditionRepository.findById(mooringRequestDto.getPennantConditionId());
                if (optionalPennantCondition.isEmpty())
                    throw new ResourceNotFoundException(String.format("No pennant condition found with the given id: %1$s", mooringRequestDto.getPennantConditionId()));
                mooring.setPennantCondition(optionalPennantCondition.get());
            } else {
                throw new RuntimeException("Pennant condition cannot be null");
            }

            mooring.setLastModifiedDate(new Date(System.currentTimeMillis()));
            savedMooring = mooringRepository.save(mooring);
            Mooring finalSavedMooring = savedMooring;

            if(null != optionalBoatyard.get().getMooringList()) optionalBoatyard.get().getMooringList()
                    .removeIf(mooring1 -> mooring1.getId().equals(finalSavedMooring.getId()));
            else  optionalBoatyard.get().setMooringList(new ArrayList<>());

            if(null != optionalCustomer.get().getMooringList()) optionalCustomer.get().getMooringList()
                            .removeIf(mooring1 -> mooring1.getId().equals(finalSavedMooring.getId()));
            else optionalCustomer.get().setMooringList(new ArrayList<>());

            optionalBoatyard.get().getMooringList().add(finalSavedMooring);
            optionalCustomer.get().getMooringList().add(finalSavedMooring);
            boatyardRepository.save(optionalBoatyard.get());
            customerRepository.save(optionalCustomer.get());
            return savedMooring;
        } catch (Exception e) {
            log.error("Error occurred during performSave() function {}", e.getLocalizedMessage());
            throw new DBOperationException(e.getMessage(), e);
        }
    }
}
