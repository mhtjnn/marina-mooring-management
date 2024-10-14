package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.MooringRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;


/**
 * Service interface for managing Mooring entities.
 */
public interface MooringService {

    /**
     * Fetches a list of moorings based on the provided search request parameters and search text.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @param searchText the text used to search for specific moorings by name, location, or other relevant criteria.
     * @return a BasicRestResponse containing the results of the mooring search.
     */
    BasicRestResponse fetchMoorings(final BaseSearchRequest baseSearchRequest, final String searchText, final HttpServletRequest request);

    /**
     * Saves a mooring based on the provided request DTO.
     *
     * @param dto the mooring request DTO
     */
    BasicRestResponse saveMooring(final MooringRequestDto dto, final HttpServletRequest request);

    /**
     * Updates a mooring based on the provided request DTO and mooring ID.
     *
     * @param mooringRequestDto the mooring request DTO
     * @param mooringId         the mooring ID
     */
    BasicRestResponse updateMooring(final MooringRequestDto mooringRequestDto, final Integer mooringId, final HttpServletRequest request);

    /**
     * Deletes a mooring based on the provided ID.
     *
     * @param id the mooring ID
     * @return a message indicating the deletion status
     */
    BasicRestResponse deleteMooring(final Integer id, final HttpServletRequest request);

    BasicRestResponse getMooringById(final Integer id, final HttpServletRequest request);

    BasicRestResponse fetchMooringPercentageIncrease(final HttpServletRequest request);
}