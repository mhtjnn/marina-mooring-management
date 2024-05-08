package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.MooringSearchRequest;
import com.marinamooringmanagement.model.request.MooringRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;


/**
 * Service interface for managing Mooring entities.
 */
public interface MooringService {

    BasicRestResponse fetchMoorings(final MooringSearchRequest mooringSearchRequest);

    /**
     * Saves a mooring based on the provided request DTO.
     *
     * @param dto the mooring request DTO
     */
    BasicRestResponse saveMooring(final MooringRequestDto dto);

    /**
     * Updates a mooring based on the provided request DTO and mooring ID.
     *
     * @param mooringRequestDto the mooring request DTO
     * @param mooringId         the mooring ID
     */
    BasicRestResponse updateMooring(final MooringRequestDto mooringRequestDto, final Integer mooringId);

    /**
     * Deletes a mooring based on the provided ID.
     *
     * @param id the mooring ID
     * @return a message indicating the deletion status
     */
    BasicRestResponse deleteMooring(final Integer id);
}