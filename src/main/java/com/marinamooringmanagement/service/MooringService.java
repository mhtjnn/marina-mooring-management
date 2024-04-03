package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.MooringRequestDto;
import com.marinamooringmanagement.model.response.MooringResponseDto;

import java.util.List;


/**
 * Service interface for managing Mooring entities.
 */
public interface MooringService {

    /**
     * Fetches a paginated list of moorings.
     *
     * @param page    the page number
     * @param size    the page size
     * @param sortBy  the field to sort by
     * @param sortDir the sort direction (asc or desc)
     * @return a list of mooring response DTOs
     */
    List<MooringResponseDto> fetchMoorings(Integer page, Integer size, String sortBy, String sortDir);

    /**
     * Saves a mooring based on the provided request DTO.
     *
     * @param dto the mooring request DTO
     */
    void saveMooring(MooringRequestDto dto);

    /**
     * Updates a mooring based on the provided request DTO and mooring ID.
     *
     * @param mooringRequestDto the mooring request DTO
     * @param mooringId         the mooring ID
     */
    void updateMooring(MooringRequestDto mooringRequestDto, Integer mooringId);

    /**
     * Deletes a mooring based on the provided ID.
     *
     * @param id the mooring ID
     * @return a message indicating the deletion status
     */
    String deleteMooring(Integer id);
}

