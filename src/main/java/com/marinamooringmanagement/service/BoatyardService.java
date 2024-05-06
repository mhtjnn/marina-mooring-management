package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.dto.BoatyardDto;
import com.marinamooringmanagement.model.request.BoatyardRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;

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
     * Retrieves a list of BoatYard entities.
     *
     * @param pageNumber The page number.
     * @param pageSize   The size of each page.
     * @param sortBy     The field to sort by.
     * @param sortDir    The direction of sorting.
     * @return A list of BoatyardDto objects.
     */
    BasicRestResponse fetchBoatyards(final Integer pageNumber, final Integer pageSize, final String sortBy, final String sortDir);

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
     * @param boatYardRequestDto The BoatYardDto containing the updated data.
     * @param id                 The ID of the BoatYard to update.
     * @return A BasicRestResponse indicating the status of the operation.
     */
    BasicRestResponse updateBoatyard(final BoatyardRequestDto boatyardRequestDto, final Integer id);
}
