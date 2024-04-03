package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.MooringMapper;
import com.marinamooringmanagement.model.entity.Mooring;
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

import org.springframework.stereotype.Service;

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
    private MooringMapper mooringMapper;

    @Autowired
    private MooringRepository mooringRepository;

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
    public List<MooringResponseDto> fetchMoorings(Integer pageNumber, Integer size, String sortBy, String sortDir) {
        try {
            log.info("API called to fetch all the moorings in the database");
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(pageNumber, size, sort);
            Page<Mooring> mooringPage = mooringRepository.findAll(pageable);
            List<MooringResponseDto> mooringResponseDtoList = mooringPage.getContent()
                    .stream()
                    .map(mooring -> mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), mooring))
                    .collect(Collectors.toList());
            return mooringResponseDtoList;
        } catch (Exception e) {
            log.error("Error occurred while fetching all the moorings in the database", e);
            throw new DBOperationException("Error occurred while fetching all the moorings in the database", e);
        }
    }

    /**
     * Saves a mooring based on the provided request DTO.
     *
     * @param mooringRequestDto the mooring request DTO
     */
    @Override
    public void saveMooring(MooringRequestDto mooringRequestDto) {
        try {
            log.info("API called to save the mooring in the database");
            Mooring mooring = new Mooring();
            performSave(mooringRequestDto, mooring, null);
        } catch (Exception e) {
            log.error("Error occurred while saving the mooring in the database", e);
            throw new DBOperationException("Error occurred while saving the mooring in the database", e);
        }
    }

    /**
     * Updates a mooring based on the provided request DTO and mooring ID.
     *
     * @param mooringRequestDto the mooring request DTO
     * @param mooringId         the mooring ID
     */
    @Override
    public void updateMooring(MooringRequestDto mooringRequestDto, Integer mooringId) {
        try {
            log.info("API called to update the mooring");
            if (mooringId == null) {
                throw new IllegalArgumentException("Mooring Id not provided for update request");
            }
            Optional<Mooring> optionalMooring = mooringRepository.findById(mooringId);
            Mooring mooring = optionalMooring.orElseThrow(() -> new ResourceNotFoundException("Mooring not found with id: " + mooringId));
            performSave(mooringRequestDto, mooring, mooringId);
        } catch (Exception e) {
            log.error("Error occurred while updating mooring", e);
            throw new DBOperationException("Error occurred while updating mooring", e);
        }
    }

    /**
     * Deletes a mooring based on the provided ID.
     *
     * @param id the mooring ID
     * @return a message indicating the deletion status
     */
    @Override
    public String deleteMooring(Integer id) {
        try {
            mooringRepository.deleteById(id);
            return "Mooring with id " + id + " deleted successfully";
        } catch (Exception e) {
            log.error("Error occurred while deleting mooring with id " + id, e);
            throw new DBOperationException("Error occurred while deleting mooring with id " + id, e);
        }
    }

    /**
     * Performs the actual saving of a mooring entity based on the request DTO and mooring object.
     *
     * @param mooringRequestDto the mooring request DTO
     * @param mooring           the mooring object to be saved or updated
     * @param id                the mooring ID (null for new moorings)
     */
    public void performSave(MooringRequestDto mooringRequestDto, Mooring mooring, Integer id) {
        try {
            log.info("performSave() function called");
            if (id == null) {
                mooring.setCreationDate(new Date(System.currentTimeMillis()));
            }
            mooring.setLastModifiedDate(new Date(System.currentTimeMillis()));
            mooringMapper.mapToMooring(mooring, mooringRequestDto);
            mooringRepository.save(mooring);
        } catch (Exception e) {
            log.error("Error occurred during performSave() function", e);
            throw new DBOperationException("Error occurred during performSave() function", e);
        }
    }
}
