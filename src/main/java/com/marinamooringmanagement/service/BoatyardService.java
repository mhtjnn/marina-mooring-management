package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.dto.BoatyardDto;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.BoatyardRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Service interface for managing BoatYard entities.
 * Defines methods for performing CRUD operations on BoatYard entities.
 */
public interface BoatyardService {
    /**
     * Saves a BoatYard entity.
     *
     * @param boatyardRequestDto The BoatYardDto containing the data to be saved.
     */
    BasicRestResponse saveBoatyard(final BoatyardRequestDto boatyardRequestDto);

    /**
     * Fetches a list of boatyards based on the provided search request parameters and search text.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @param searchText the text used to search for specific boatyards by name, location, or other relevant criteria.
     * @return a BasicRestResponse containing the results of the boatyard search.
     */
    BasicRestResponse fetchBoatyards(final BaseSearchRequest baseSearchRequest, final String searchText, final HttpServletRequest request);

    /**
     * Retrieves a BoatYard entity by its ID.
     *
     * @param id The ID of the BoatYard to retrieve.
     * @return The BoatYardDto object corresponding to the given ID.
     */
    BoatyardDto getbyId(final Integer id);

    /**
     * Deletes a BoatYard entity by its ID.
     *
     * @param id The ID of the BoatYard to delete.
     */
    BasicRestResponse deleteBoatyardById(final Integer id);

    /**
     * Updates a BoatYard entity.
     *
     * @param boatyardRequestDto The BoatYardDto containing the updated data.
     * @param id                 The ID of the BoatYard to update.
     * @return A BasicRestResponse indicating the status of the operation.
     */
    BasicRestResponse updateBoatyard(final BoatyardRequestDto boatyardRequestDto, final Integer id);

    /**
     * Fetches Moorings related to a specific boatyard from the database.
     *
     * @param id the ID of the boatyard.
     * @return a {@link BasicRestResponse} containing the moorings related to the boatyard.
     */
    BasicRestResponse fetchMooringsWithBoatyard(final Integer id);
}
