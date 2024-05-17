package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.BoatyardMapper;
import com.marinamooringmanagement.mapper.MooringMapper;
import com.marinamooringmanagement.model.dto.BoatyardDto;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.request.BoatyardRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.BoatyardResponseDto;
import com.marinamooringmanagement.model.response.MooringResponseDto;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.service.BoatyardService;
import com.marinamooringmanagement.utils.ConversionUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private ConversionUtils conversionUtils;

    private static final Logger log = LoggerFactory.getLogger(BoatyardServiceImpl.class);

    /**
     * Saves a BoatYard entity.
     *
     * @param boatYardRequestDto The BoatYardDto containing the data to be saved.
     */
    @Override
    public BasicRestResponse saveBoatyard(final BoatyardRequestDto boatYardRequestDto) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Boatyard boatyard = new Boatyard();
            performSave(boatYardRequestDto, boatyard, null);
            response.setMessage("BoatYard Saved Successfully");
            response.setStatus(HttpStatus.CREATED.value());
            log.info(String.format("Saving data in the database for BoatYard ID %d", boatYardRequestDto.getId()));

        } catch (Exception e) {
            log.error(String.format("Exception occurred while performing save operation: %s", e.getMessage()), e);

            throw new DBOperationException(e.getMessage(), e);
        }
        return response;
    }

    /**
     * Retrieves a list of BoatYard entities.
     *
     * @param pageNumber The page number.
     * @param pageSize   The size of each page.
     * @param sortBy     The field to sort by.
     * @param sortDir    The direction of sorting.
     * @return A list of BoatYardDto objects.
     */
    public BasicRestResponse fetchBoatyards(final Integer pageNumber, final Integer pageSize, final String sortBy, final String sortDir, final String searchText) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            Specification<Boatyard> spec = new Specification<Boatyard>() {
                @Override
                public Predicate toPredicate(Root<Boatyard> boatyard, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if (null != searchText) {
                        if(conversionUtils.isInteger(searchText)) {
                            predicates.add(criteriaBuilder.or(
                                    criteriaBuilder.equal(boatyard.get("id"), Integer.parseInt(searchText)),
                                    criteriaBuilder.like(boatyard.get("address"), "%" + searchText + "%")
                            ));
                        }
                        else {
                            predicates.add(criteriaBuilder.or(
                                    criteriaBuilder.like(boatyard.get("name"), "%" + searchText + "%"),
                                    criteriaBuilder.like(boatyard.get("address"), "%" + searchText + "%")
                            ));
                        }
                    }

                    return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };

            List<Boatyard> boatyardList = boatYardRepository.findAll(spec);

            final Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            final Pageable p = PageRequest.of(pageNumber, pageSize, sort);
            final Page<Boatyard> pageUser = boatYardRepository.findAll(p);
//            List<Boatyard> boatyardList = pageUser.getContent();
            response.setMessage("All boatyard are fetched successfully");
            response.setStatus(HttpStatus.OK.value());

            final List<BoatyardResponseDto> boatyardDtoList = boatyardList.stream()
                    .map(boatyard -> {
                        BoatyardResponseDto boatyardResponseDto =  BoatyardResponseDto.builder().build();
                        boatyardMapper.mapToBoatYardResponseDto(boatyardResponseDto, boatyard);

                        boatyardResponseDto.setState(boatyard.getState().getName());
                        boatyardResponseDto.setCountry(boatyard.getCountry().getName());

                        List<MooringResponseDto> mooringResponseDtoList = boatyard.getMooringList()
                                .stream()
                                .map(mooring -> {
                                    MooringResponseDto mooringResponseDto = mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), mooring);
                                    return mooringResponseDto;
                                })
                                .toList();
                        boatyardResponseDto.setMooringResponseDtoList(mooringResponseDtoList);
                        return boatyardResponseDto;
                    })
                    .collect(Collectors.toList());
            response.setContent(boatyardDtoList);
            log.info(String.format("BoatYard fetched successfully"));
        } catch (Exception e) {
            log.error(String.format("Error occurred while fetching BoatYard: %s", e.getMessage()), e);
            throw new DBOperationException(e.getMessage(), e);
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
    public BasicRestResponse deleteBoatyardById(final Integer id) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            Optional<Boatyard> optionalBoatyard = boatYardRepository.findById(id);

            if(optionalBoatyard.isEmpty()) throw new DBOperationException(String.format("No boatyard found with the given id: %1$s", id));

            if(null == optionalBoatyard.get().getBoatyardName()) throw new RuntimeException(String.format("Boatyard Name is not found in the boatyard entity with the id as %1$s", id));

            List<Mooring> mooringList = mooringRepository.findAllByBoatyardName(optionalBoatyard.get().getBoatyardName());

            mooringRepository.deleteAllByBoatyardName(optionalBoatyard.get().getBoatyardName());

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
    public BasicRestResponse updateBoatyard(final BoatyardRequestDto boatYardRequestDto, final Integer id) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        try {
            if (null == id) {
                log.info(String.format("Update attempt without a BoatYard ID provided in the request DTO"));

                throw new ResourceNotFoundException(String.format("BoatYard not found with id: %1$s", id));
            }
            Optional<Boatyard> optionalBoatYard = boatYardRepository.findById(id);
            if (optionalBoatYard.isPresent()) {
                Boatyard boatyard = optionalBoatYard.get();
                performSave(boatYardRequestDto, boatyard, id);
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
     * Performs the saving operation for a Boatyard entity.
     *
     * @param boatyardRequestDto The BoatyardRequestDto containing the data to be saved.
     * @param boatyard           The Boatyard entity to be saved or updated.
     * @param id                 The ID of the Boatyard entity, if available.
     * @throws DBOperationException If an error occurs during the saving operation.
     */
    public void performSave(final BoatyardRequestDto boatyardRequestDto, final Boatyard boatyard, Integer id) {
        try {
            boatyard.setLastModifiedDate(new Date(System.currentTimeMillis()));

            if(StringUtils.isNotEmpty(boatyardRequestDto.getBoatyardName())
                    && StringUtils.isNotEmpty(boatyard.getBoatyardName())
                    && !StringUtils.equals(boatyardRequestDto.getBoatyardName(), boatyard.getBoatyardName())) {

                List<Mooring> mooringList = boatyard.getMooringList().stream().map(mooring -> {
                    mooring.setBoatyardName(boatyardRequestDto.getBoatyardName());
                    return mooring;
                }).toList();

                mooringRepository.saveAll(mooringList);
            }

            boatyardMapper.mapToBoatYard(boatyard, boatyardRequestDto);

            if(null == id) boatyard.setCreationDate(new Date());
            boatyard.setLastModifiedDate(new Date());

            if (null != boatyardRequestDto.getState()) {
                final Optional<State> optionalState = stateRepository.findByName(boatyardRequestDto.getState());
                if (optionalState.isPresent()) {
                    boatyard.setState(optionalState.get());
                } else {
                    State state = State.builder()
                            .name(boatyardRequestDto.getState())
                            .build();
                    boatyard.setState(stateRepository.save(state));
                }
            }

            if (null != boatyardRequestDto.getCountry()) {
                final Optional<Country> optionalCountry = countryRepository.findByName(boatyardRequestDto.getCountry());
                if (optionalCountry.isPresent()) {
                    boatyard.setCountry(optionalCountry.get());
                } else {
                    Country country = Country.builder()
                            .name(boatyardRequestDto.getCountry())
                            .build();
                    boatyard.setCountry(countryRepository.save(country));
                }
            }

            boatYardRepository.save(boatyard);

            log.info(String.format("Boatyard saved successfully with ID: %d", boatyard.getId()));
        } catch (Exception e) {
            log.error(String.format("Error occurred during performSave() function: %s", e.getMessage()), e);
            throw new DBOperationException(e.getMessage(), e);
        }
    }
}