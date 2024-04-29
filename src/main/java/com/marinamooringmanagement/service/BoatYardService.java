package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.dto.BoatYardDto;
import com.marinamooringmanagement.model.request.BoatYardRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;

/**
 * Service interface for managing BoatYard entities.
 * Defines methods for performing CRUD operations on BoatYard entities.
 */
public interface BoatYardService {
    /**
     * Saves a BoatYard entity.
     *
     * @param boatYardRequestDto The BoatYardDto containing the data to be saved.
     */
    BasicRestResponse saveBoatYard(final BoatYardRequestDto boatYardRequestDto);

    /**
     * Retrieves a list of BoatYard entities.
     *
     * @param pageNumber The page number.
     * @param pageSize   The size of each page.
     * @param sortBy     The field to sort by.
     * @param sortDir    The direction of sorting.
     * @return A list of BoatYardDto objects.
     */
    BasicRestResponse getBoatYard(final Integer pageNumber, final Integer pageSize, final String sortBy, final String sortDir);

    /**
     * Retrieves a BoatYard entity by its ID.
     *
     * @param id The ID of the BoatYard to retrieve.
     * @return The BoatYardDto object corresponding to the given ID.
     */
    BoatYardDto getbyId(final Integer id);

    /**
     * Deletes a BoatYard entity by its ID.
     *
     * @param id The ID of the BoatYard to delete.
     */
    BasicRestResponse deleteBoatYardbyId(final Integer id);

    /**
     * Updates a BoatYard entity.
     *
     * @param boatYardRequestDto The BoatYardDto containing the updated data.
     * @param id                 The ID of the BoatYard to update.
     * @return A BasicRestResponse indicating the status of the operation.
     */
    BasicRestResponse updateBoatYard(final BoatYardRequestDto boatYardRequestDto, final Integer id);
}
