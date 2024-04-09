package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.MooringMapper;
import com.marinamooringmanagement.model.dto.CustomerDto;
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
    public BasicRestResponse fetchMoorings(Integer pageNumber, Integer size, String sortBy, String sortDir) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to fetch all the moorings in the database");
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(pageNumber, size, sort);
            Page<Mooring> mooringPage = mooringRepository.findAll(pageable);
            List<MooringResponseDto> mooringResponseDtoList = mooringPage.getContent()
                    .stream()
                    .map(mooring -> mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), mooring))
                    .collect(Collectors.toList());
            response.setMessage("All moorings fetched successfully.");
            response.setStatus(HttpStatus.OK.value());

            CustomerDto customerDto = CustomerDto.builder().build();
            customerDto.setId(1);
            customerDto.setCustomerName("John Doe");
            customerDto.setNote("Test");
            customerDto.setCountry("India");
            customerDto.setPhone("123456789");
            customerDto.setState("Punjab");
            customerDto.setPinCode("1234");
            customerDto.setSectorBlock("1234");
            customerDto.setEmailAddress("test@gmail.com");
            customerDto.setStreetHouse("test");

            MooringResponseDto mooringResponseDto = null;

            if(!mooringResponseDtoList.isEmpty())  mooringResponseDto = mooringResponseDtoList.get(0);

            response.setContent(List.of(mooringResponseDtoList, customerDto, mooringResponseDto));
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
    public BasicRestResponse saveMooring(MooringRequestDto mooringRequestDto) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to save the mooring in the database");
            Mooring mooring = new Mooring();
            performSave(mooringRequestDto, mooring, null);
            response.setMessage("Mooring saved successfully.");
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            log.error("Error occurred while saving the mooring in the database", e);
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
    public BasicRestResponse updateMooring(MooringRequestDto mooringRequestDto, Integer mooringId) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to update the mooring with the given mooring ID");
            if (mooringId == null) {
                throw new IllegalArgumentException("Mooring Id not provided for update request");
            }
            Optional<Mooring> optionalMooring = mooringRepository.findById(mooringId);
            Mooring mooring = optionalMooring.orElseThrow(() -> new ResourceNotFoundException("Mooring not found with id: " + mooringId));
            performSave(mooringRequestDto, mooring, mooringId);
            response.setMessage("Mooring updated successfully");
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("Error occurred while updating mooring", e);
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
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            mooringRepository.deleteById(id);
            String message = "Mooring with id " + id + " deleted successfully";
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
