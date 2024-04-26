package com.marinamooringmanagement.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.BoatYardMapper;
import com.marinamooringmanagement.model.dto.BoatYardDto;
import com.marinamooringmanagement.model.entity.BoatYard;
import com.marinamooringmanagement.model.request.BoatYardRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.repositories.BoatYardRepository;
import com.marinamooringmanagement.service.BoatYardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Implementation of the BoatYardService interface.
 * Provides methods for CRUD operations on BoatYard entities.
 */
@Service
public class BoatYardServiceImpl implements BoatYardService {

    @Autowired
    private BoatYardRepository boatYardRepository;

    @Autowired
    private BoatYardMapper boatYardMapper;

    private static final Logger log = LoggerFactory.getLogger(BoatYardServiceImpl.class);

    /**
     * Saves a BoatYard entity.
     *
     * @param boatYardRequestDto The BoatYardDto containing the data to be saved.
     */
    @Override
    public BasicRestResponse saveBoatYard(final BoatYardRequestDto boatYardRequestDto) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final BoatYard boatYard = new BoatYard();
            performSave(boatYardRequestDto, boatYard, null);
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
    public BasicRestResponse getBoatYard(final Integer pageNumber, final Integer pageSize, final String sortBy, final String sortDir) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            final Pageable p = PageRequest.of(pageNumber, pageSize, sort);
            final Page<BoatYard> pageUser = boatYardRepository.findAll(p);
            List<BoatYard> boatYardList = pageUser.getContent();
            response.setMessage("All boatyard are fetched successfully");
            response.setStatus(HttpStatus.OK.value());

            final List<BoatYardDto> boatYardDtoList = boatYardList.stream()
                    .map(boatYardMapper::toDto)
                    .collect(Collectors.toList());
            response.setContent(boatYardDtoList);
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
    public BoatYardDto getbyId(final Integer id) {
        if (id == null) {
            throw new RuntimeException("ID cannot be null");
        }
        try {
            Optional<BoatYard> BoatYardEntityOptional = boatYardRepository.findById(id);
            if (BoatYardEntityOptional.isPresent()) {
                return boatYardMapper.toDto(BoatYardEntityOptional.get());
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
    public BasicRestResponse deleteBoatYardbyId(final Integer id) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
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
    public BasicRestResponse updateBoatYard(final BoatYardRequestDto boatYardRequestDto, final Integer id) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        try {
            if (null == boatYardRequestDto.getId()) {
                log.info(String.format("Update attempt without a BoatYard ID provided in the request DTO"));

                throw new ResourceNotFoundException(String.format("BoatYard not found with id: %1$s", id));
            }
            Optional<BoatYard> optionalBoatYard = boatYardRepository.findById(id);
            if (optionalBoatYard.isPresent()) {
                BoatYard boatYard = optionalBoatYard.get();
                performSave(boatYardRequestDto, boatYard, boatYardRequestDto.getId());
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
     * Helper method to perform the save operation for a BoatYard entity.
     *
     * @param boatYardRequestDto The BoatYardDto containing the data.
     * @param boatYard           The BoatYard entity to be updated.
     * @param id                 The ID of the BoatYard to update.
     */
    public void performSave(final BoatYardRequestDto boatYardRequestDto, final BoatYard boatYard, final Integer id) {

        try {
            if (null == id) {

                boatYard.setLastModifiedDate(new Date(System.currentTimeMillis()));

            }
            boatYardMapper.mapToBoatYard(boatYard, boatYardRequestDto);
            boatYard.setCreationDate(new Date());
            boatYard.setLastModifiedDate(new Date());

            boatYardRepository.save(boatYard);

            log.info(String.format("BoatYard saved successfully with ID: %d", boatYard.getId()));
        } catch (Exception e) {
            log.error(String.format("Error occurred during performSave() function: %s", e.getMessage()), e);
            throw new DBOperationException(e.getMessage(), e);
        }
    }
}