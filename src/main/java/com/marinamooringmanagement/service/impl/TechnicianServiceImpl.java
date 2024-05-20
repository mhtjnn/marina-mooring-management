package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.TechnicianMapper;
import com.marinamooringmanagement.model.dto.TechnicianDto;
import com.marinamooringmanagement.model.entity.Technician;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.TechnicianRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.TechnicianResponseDto;
import com.marinamooringmanagement.repositories.TechnicianRepository;
import com.marinamooringmanagement.service.TechnicianService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the TechnicianService interface.
 */
@Service
public class TechnicianServiceImpl implements TechnicianService {
    private static final Logger log = LoggerFactory.getLogger(TechnicianServiceImpl.class);

    @Autowired
    private TechnicianRepository technicianRepository;

    @Autowired
    private TechnicianMapper technicianMapper;

    @Autowired
    private SortUtils sortUtils;

    /**
     * Saves a new technician.
     *
     * @param technicianRequestDto The DTO containing technician information.
     */
    @Override
    public BasicRestResponse saveTechnician(final TechnicianRequestDto technicianRequestDto) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));


        try {
            final Technician technician = new Technician();
            performSave(technicianRequestDto, technician, null);
            response.setStatus(HttpStatus.CREATED.value());
            response.setMessage("Technician saved in the database");
            log.info(String.format("Saving data in the database for Technician ID %d", technicianRequestDto.getId()));

        } catch (Exception e) {
            log.error(String.format("Exception occurred while performing save operation: %s", e.getMessage()), e);

            throw new DBOperationException(e.getMessage(), e);
        }
        return response;
    }


    /**
     * Fetches a list of technicians based on the provided search request parameters and search text.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @param searchText the text used to search for specific technicians by name, skill, location, or other relevant criteria.
     * @return a BasicRestResponse containing the results of the technician search.
     */
    @Override
    public BasicRestResponse fetchTechnicians(final BaseSearchRequest baseSearchRequest, final String searchText) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info(String.format("Technicians fetched successfully"));

            final Specification<Technician> spec = new Specification<Technician>() {
                @Override
                public Predicate toPredicate(Root<Technician> technician, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    if (null != searchText) {
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(technician.get("technicianName"), "%" + searchText + "%"),
                                criteriaBuilder.like(technician.get("emailAddress"), "%" + searchText + "%")
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

            final Page<Technician> techniciansList = technicianRepository.findAll(spec, p);

            if(null == techniciansList || techniciansList.getContent().isEmpty()) throw new ResourceNotFoundException("No technician found");

            List<TechnicianResponseDto> technicianResponseDtoList = techniciansList.getContent().stream()
                    .map(technician -> technicianMapper.mapToTechnicianResponseDto(TechnicianResponseDto.builder().build(), technician))
                    .collect(Collectors.toList());

            response.setMessage("List of technicians in the database");
            response.setContent(technicianResponseDtoList);
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            log.error(String.format("Error occurred while fetching Technicians: %s", e.getMessage()), e);

            response.setMessage("Error occurred while fetching list of technician from the database");
            response.setContent(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Retrieves a technician by ID.
     *
     * @param id The ID of the technician.
     * @return The TechnicianDto object.
     */
    @Override
    public TechnicianDto getbyId(final Integer id) {
        if (id == null) {
            throw new ResourceNotFoundException("ID cannot be null");
        }
        try {
            Optional<Technician> technicianEntityOptional = technicianRepository.findById(id);
            if (technicianEntityOptional.isPresent()) {
                log.info(String.format("Successfully retrieved Technician data for ID: %d", id));

                return technicianMapper.toDto(technicianEntityOptional.get());


            } else {
                throw new ResourceNotFoundException("Technician with ID : " + id + " doesn't exist");
            }
        } catch (Exception e) {
            log.error(String.format("Error occurred while retrieving Technician for ID: %d: %s", id, e.getMessage()), e);

            throw new DBOperationException(e.getMessage(), e);
        }
    }

    /**
     * Deletes a technician by ID.
     *
     * @param id The ID of the technician to delete.
     */
    @Override
    public BasicRestResponse deleteTechnicianbyId(final Integer id) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setMessage("Deleted technician");
        response.setStatus(HttpStatus.OK.value());
        try {
            technicianRepository.deleteById(id);
            response.setMessage(String.format("Technician with ID %d deleted successfully", id));
            log.info(String.format("Technician with ID %d deleted successfully", id));

        } catch (Exception e) {
            log.error(String.format("Error occurred while deleting Technician with ID %d", id));

            throw new DBOperationException(e.getMessage(), e);
        }
        return response;
    }


    /**
     * Updates a Technician entity.
     *
     * @param technicianRequestDto The TechnicianDto containing the updated data.
     * @param id                   The ID of the Technician to update.
     * @return A BasicRestResponse indicating the status of the operation.
     * @throws DBOperationException if the technician ID is not provided or if an error occurs during the operation.
     */
    @Override
    public BasicRestResponse updateTechnician(final TechnicianRequestDto technicianRequestDto, final Integer id) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        try {
            if (null == technicianRequestDto.getId()) {
                log.info(String.format("Update attempt without a Technician ID provided in the request DTO"));

                throw new ResourceNotFoundException("Technician Id not provided for update request");
            }
            Optional<Technician> optionalTechnician = technicianRepository.findById(id);
            if (optionalTechnician.isPresent()) {


                Technician technician = optionalTechnician.get();
                performSave(technicianRequestDto, technician, technicianRequestDto.getId());
                response.setMessage("Technician with the given technician id updated successfully!!!");
                response.setStatus(HttpStatus.OK.value());
            }
        } catch (Exception e) {
            log.error(String.format("Error occurred while updating technician: %s", e.getMessage()), e);

            throw new DBOperationException(e.getMessage(), e);
        }
        return response;

    }

    /**
     * Helper method to perform the save operation for a Technician entity.
     *
     * @param technicianRequestDto The TechnicianDto containing the data.
     * @param technician           The Technician entity to be updated.
     * @param id                   The ID of the Technician to update.
     * @throws DBOperationException if an error occurs during the save operation.
     */
    public void performSave(final TechnicianRequestDto technicianRequestDto, final Technician technician, final Integer id) {
        try {
            if (null == id) technician.setCreationDate(new Date(System.currentTimeMillis()));
            technicianMapper.mapToTechnician(technician, technicianRequestDto);
            technician.setLastModifiedDate(new Date());
            technicianRepository.save(technician);
            log.info(String.format("Technician saved successfully with ID: %d", technician.getId()));


        } catch (Exception e) {
            log.error(String.format("Error occurred during performSave() function: %s", e.getMessage()), e);

            throw new DBOperationException(e.getMessage(), e);
        }
    }
}