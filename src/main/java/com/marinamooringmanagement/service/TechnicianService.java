package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.dto.TechnicianDto;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.TechnicianRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Service interface for managing Technician entities.
 */
public interface TechnicianService {
    /**
     * Saves a new technician.
     *
     * @param technicianRequestDto The DTO containing technician information.
     */
    BasicRestResponse saveTechnician(final TechnicianRequestDto technicianRequestDto, final HttpServletRequest request);

    /**
     * Fetches a list of technicians based on the provided search request parameters and search text.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @param searchText the text used to search for specific technicians by name, skill, location, or other relevant criteria.
     * @return a BasicRestResponse containing the results of the technician search.
     */
    BasicRestResponse fetchTechnicians(final BaseSearchRequest baseSearchRequest, final String searchText, final HttpServletRequest request);

    /**
     * Retrieves a technician by ID.
     *
     * @param id The ID of the technician.
     * @return The TechnicianDto object.
     */
    TechnicianDto getbyId(final Integer id, final HttpServletRequest request);

    /**
     * Deletes a technician by ID.
     *
     * @param id The ID of the technician to delete.
     */
    BasicRestResponse deleteTechnicianbyId(final Integer id, final HttpServletRequest request);

    /**
     * Updates the details of a technician based on the provided technician request data and technician ID.
     *
     * @param technicianRequestDto the data transfer object containing the updated details of the technician.
     * @param id the unique identifier of the technician to be updated.
     * @return a BasicRestResponse indicating the success or failure of the update operation.
     */
    BasicRestResponse updateTechnician(final TechnicianRequestDto technicianRequestDto, final Integer id, final HttpServletRequest request);
}