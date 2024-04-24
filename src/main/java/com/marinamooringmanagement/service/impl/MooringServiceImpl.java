package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.MooringMapper;
import com.marinamooringmanagement.model.entity.Mooring;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.MooringResponseDto;
import com.marinamooringmanagement.repositories.MooringRepository;
import com.marinamooringmanagement.model.request.MooringRequestDto;
import com.marinamooringmanagement.service.MooringService;
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
 * Service implementation for managing Mooring entities.
 */
@Service
public class MooringServiceImpl implements MooringService {

    private static final Logger log = LoggerFactory.getLogger(MooringServiceImpl.class);

    @Autowired
    private MooringMapper mapper;

    @Autowired
    private MooringRepository mooringRepository;

    public MooringServiceImpl() {}

    /**
     * Fetches a paginated list of moorings.
     *
     * @param pageNumber the page number
     * @param size       the page size
     * @param sortBy     the field to sort by
     * @param sortDir    the sort direction (asc or desc)
     * @return a list of mooring response DTOs
     */
    @Override
    public BasicRestResponse fetchMoorings(final Integer pageNumber, final Integer size, final String sortBy, final String sortDir) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to fetch all the moorings in the database");
            final Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            final Pageable pageable = PageRequest.of(pageNumber, size, sort);
            final Page<Mooring> mooringPage = mooringRepository.findAll(pageable);
            List<MooringResponseDto> mooringResponseDtoList = mooringPage.getContent()
                    .stream()
                    .map(mooring -> mapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), mooring))
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
            mapper.mapToMooring(mooring, mooringRequestDto);
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
            if(optionalMooring.isEmpty()) throw new RuntimeException(String.format("No mooring exists with %1$s", id));
            mooringRepository.deleteById(id);
            Optional<Mooring> optionalMooringAfterDeleteOperation = mooringRepository.findById(id);
            final String message = optionalMooringAfterDeleteOperation.isPresent() ? String.format("Mooring with id %1$s deleted successfully", id) : String.format("Failed to delete mooring with the given id %1$s", id);
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
            if (id == null) {
                mooring.setCreationDate(new Date(System.currentTimeMillis()));
            }
            mooring.setLastModifiedDate(new Date(System.currentTimeMillis()));
            mapper.mapToMooring(mooring, mooringRequestDto);
            return mooringRepository.save(mooring);
        } catch (Exception e) {
            log.error("Error occurred during performSave() function {}", e.getLocalizedMessage());
            throw new DBOperationException("Error occurred during performSave() function", e);
        }
    }
}
