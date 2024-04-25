package com.marinamooringmanagement.service;


import com.marinamooringmanagement.model.dto.TechnicianDto;
import com.marinamooringmanagement.model.request.TechnicianRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;


/**
 * Service interface for managing Technician entities.
 */
public interface TechnicianService {
    /**
     * Saves a new technician.
     *
     * @param technicianRequestDto The DTO containing technician information.
     */
    BasicRestResponse saveTechnician(final TechnicianRequestDto technicianRequestDto);

    /**
     * Retrieves a list of technicians with pagination and sorting.
     *
     * @param pageNumber The page number.
     * @param pageSize   The page size.
     * @param sortBy     The field to sort by.
     * @param sortDir    The sorting direction.
     * @return A list of TechnicianDto objects.
     */
    public BasicRestResponse getTechnicians(final int pageNumber, final int pageSize, final String sortBy, final String sortDir);

    /**
     * Retrieves a technician by ID.
     *
     * @param id The ID of the technician.
     * @return The TechnicianDto object.
     */
    TechnicianDto getbyId(final Integer id);

    /**
     * Deletes a technician by ID.
     *
     * @param id The ID of the technician to delete.
     */
    BasicRestResponse deleteTechnicianbyId(final Integer id);

    BasicRestResponse updateTechnician(final TechnicianRequestDto technicianRequestDto, final Integer id);


}
