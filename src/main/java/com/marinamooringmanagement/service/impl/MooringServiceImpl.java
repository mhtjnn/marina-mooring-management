package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.CustomerMapper;
import com.marinamooringmanagement.mapper.MooringMapper;
import com.marinamooringmanagement.mapper.MooringStatusMapper;
import com.marinamooringmanagement.model.dto.MooringStatusDto;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.MooringResponseDto;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.model.request.MooringRequestDto;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
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

    /**
     * Fetches a list of moorings based on the provided search request parameters and search text.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @param searchText        the text used to search for specific moorings by name, location, or other relevant criteria.
     * @return a BasicRestResponse containing the results of the mooring search.
     */
    @Override
    public BasicRestResponse fetchMoorings(final BaseSearchRequest baseSearchRequest, final String searchText) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to fetch all the moorings in the database");

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
                        if (null != mooring.getMooringStatus()) {
                            mooringResponseDto.setMooringStatusDto(mooringStatusMapper.mapToMooringStatusDto(MooringStatusDto.builder().build(), mooring.getMooringStatus()));
                        }
                        if (null != mooring.getCustomer())
                            mooringResponseDto.setCustomerId(mooring.getCustomer().getId());
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
            Optional<Mooring> optionalMooring = mooringRepository.findById(id);
            if (optionalMooring.isEmpty()) throw new RuntimeException(String.format("No mooring exists with %1$s", id));
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
            Mooring savedMooring = null;
            mooringMapper.mapToMooring(mooring, mooringRequestDto);
            if (id == null) mooring.setCreationDate(new Date(System.currentTimeMillis()));

            if (null == mooringRequestDto.getBoatyardName()) throw new RuntimeException("Boatyard name cannot be null");
            Optional<Boatyard> optionalBoatyard = boatyardRepository.findByBoatyardName(mooring.getBoatyardName());
            if (optionalBoatyard.isEmpty())
                throw new ResourceNotFoundException("No boatyard found with the given boatyard name");

//            if (null != mooringRequestDto.getMooringId()) {
            Optional<MooringStatus> optionalMooringStatus = mooringStatusRepository.findById(1);
            if (optionalMooringStatus.isEmpty())
                throw new ResourceNotFoundException(String.format("No status found with the given id: %1$s", mooringRequestDto.getStatusId()));

            mooring.setMooringStatus(optionalMooringStatus.get());
//            } else {
//                throw new RuntimeException("Mooring Status cannot be null");
//            }

            if (null != mooringRequestDto.getBoatTypeId()) {
                Optional<BoatType> optionalBoatType = boatTypeRepository.findById(mooringRequestDto.getBoatTypeId());
                if (optionalBoatType.isEmpty())
                    throw new ResourceNotFoundException(String.format("No boat type found with the given id: %1$s", mooringRequestDto.getBoatTypeId()));
                mooring.setBoatType(optionalBoatType.get());
            } else {
                throw new RuntimeException("Boat type cannot be null");
            }

            if (null != mooringRequestDto.getSizeOfWeightId()) {
                Optional<SizeOfWeight> optionalSizeOfWeight = sizeOfWeightRepository.findById(mooringRequestDto.getBoatTypeId());
                if (optionalSizeOfWeight.isEmpty())
                    throw new ResourceNotFoundException(String.format("No Size of weight found with the given id: %1$s", mooringRequestDto.getSizeOfWeightId()));
                mooring.setSizeOfWeight(optionalSizeOfWeight.get());
            } else {
                throw new RuntimeException("Size of weight cannot be null");
            }

            if (null != mooringRequestDto.getTypeOfWeightId()) {
                Optional<TypeOfWeight> optionalTypeOfWeight = typeOfWeightRepository.findById(mooringRequestDto.getBoatTypeId());
                if (optionalTypeOfWeight.isEmpty())
                    throw new ResourceNotFoundException(String.format("No Type of weight found with the given id: %1$s", mooringRequestDto.getTypeOfWeightId()));
                mooring.setTypeOfWeight(optionalTypeOfWeight.get());
            } else {
                throw new RuntimeException("Type of weight cannot be null");
            }

            if (null != mooringRequestDto.getEyeConditionId()) {
                Optional<EyeCondition> optionalEyeCondition = eyeConditionRepository.findById(mooringRequestDto.getBoatTypeId());
                if (optionalEyeCondition.isEmpty())
                    throw new ResourceNotFoundException(String.format("No Eye condition found with the given id: %1$s", mooringRequestDto.getEyeConditionId()));
                mooring.setEyeCondition(optionalEyeCondition.get());
            } else {
                throw new RuntimeException("Eye condition cannot be null");
            }

            if (null != mooringRequestDto.getTopChainConditionId()) {
                Optional<TopChainCondition> optionalTopChainCondition = topChainConditionRepository.findById(mooringRequestDto.getBoatTypeId());
                if (optionalTopChainCondition.isEmpty())
                    throw new ResourceNotFoundException(String.format("No Top chain condition found with the given id: %1$s", mooringRequestDto.getTopChainConditionId()));
                mooring.setTopChainCondition(optionalTopChainCondition.get());
            } else {
                throw new RuntimeException("Top chain condition cannot be null");
            }

            if (null != mooringRequestDto.getBottomChainConditionId()) {
                Optional<BottomChainCondition> optionalBottomChainCondition = bottomChainConditionRepository.findById(mooringRequestDto.getBoatTypeId());
                if (optionalBottomChainCondition.isEmpty())
                    throw new ResourceNotFoundException(String.format("No Bottom chain condition found with the given id: %1$s", mooringRequestDto.getBottomChainConditionId()));
                mooring.setBottomChainCondition(optionalBottomChainCondition.get());
            } else {
                throw new RuntimeException("Bottom chain condition cannot be null");
            }

            if (null != mooringRequestDto.getShackleSwivelConditionId()) {
                Optional<ShackleSwivelCondition> optionalShackleSwivelCondition = shackleSwivelConditionRepository.findById(mooringRequestDto.getBoatTypeId());
                if (optionalShackleSwivelCondition.isEmpty())
                    throw new ResourceNotFoundException(String.format("No Shackle swivel condition found with the given id: %1$s", mooringRequestDto.getShackleSwivelConditionId()));
                mooring.setShackleSwivelCondition(optionalShackleSwivelCondition.get());
            } else {
                throw new RuntimeException("Shackle swivel condition cannot be null");
            }

            if (null != mooringRequestDto.getPennantConditionId()) {
                Optional<PennantCondition> optionalPennantCondition = pennantConditionRepository.findById(mooringRequestDto.getBoatTypeId());
                if (optionalPennantCondition.isEmpty())
                    throw new ResourceNotFoundException(String.format("No pennant condition found with the given id: %1$s", mooringRequestDto.getPennantConditionId()));
                mooring.setPennantCondition(optionalPennantCondition.get());
            } else {
                throw new RuntimeException("Pennant condition cannot be null");
            }

            mooring.setLastModifiedDate(new Date(System.currentTimeMillis()));
            savedMooring = mooringRepository.save(mooring);
            optionalBoatyard.get().getMooringList().add(savedMooring);
            boatyardRepository.save(optionalBoatyard.get());
            return savedMooring;
        } catch (Exception e) {
            log.error("Error occurred during performSave() function {}", e.getLocalizedMessage());
            throw new DBOperationException(e.getMessage(), e);
        }
    }
}
