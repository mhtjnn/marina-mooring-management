package com.marinamooringmanagement.service;


import com.marinamooringmanagement.model.dto.TechnicianDto;
import com.marinamooringmanagement.model.request.TechnicianRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;

import java.util.List;
/**
 * Service interface for managing Technician entities.
 */
public interface TechnicianService {
    /**
     * Saves a new technician.
     *
     * @param technicianRequestDto The DTO containing technician information.
     */
    BasicRestResponse saveTechnician(TechnicianRequestDto technicianRequestDto);

    /**
     * Retrieves a list of technicians with pagination and sorting.
     *
     * @param pageNumber The page number.
     * @param pageSize   The page size.
     * @param sortBy     The field to sort by.
     * @param sortDir    The sorting direction.
     * @return A list of TechnicianDto objects.
     */
    public List<TechnicianDto> getTechnicians(int pageNumber, int pageSize, String sortBy, String sortDir);

    /**
     * Retrieves a technician by ID.
     *
     * @param id The ID of the technician.
     * @return The TechnicianDto object.
     */
    TechnicianDto getbyId(Integer id);

    /**
     * Deletes a technician by ID.
     *
     * @param id The ID of the technician to delete.
     */
    BasicRestResponse deleteTechnicianbyId(Integer id);

    BasicRestResponse updateTechnician(TechnicianRequestDto technicianRequestDto, Integer id);


}
