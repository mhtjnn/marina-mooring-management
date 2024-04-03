package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.TechnicianMapper;
import com.marinamooringmanagement.model.dto.TechnicianDto;
import com.marinamooringmanagement.model.entity.Technician;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.repositories.TechnicianRepository;
import com.marinamooringmanagement.service.TechnicianService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**
 * Implementation of the TechnicianService interface.
 */
@Service
public class TechnicianServiceImpl implements TechnicianService {
    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private TechnicianRepository technicianRepository;

    @Autowired
    private TechnicianMapper technicianMapper;

    /**
     * Saves a new technician.
     *
     * @param technicianDto The DTO containing technician information.
     */
    @Override
    public void saveTechnician(TechnicianDto technicianDto) {
        Technician technician = Technician.builder().build();

        try {
            performSave(technicianDto,technician,null);
            technicianRepository.save(technician);
            log.info("Technician saved Successfully");
        } catch (Exception e) {
            log.error("Exception occurred while performing save operation " + e);
            throw new DBOperationException(e.getMessage(), e);
        }

    }


    /**
     * Retrieves a list of technicians with pagination and sorting.
     *
     * @param pageNumber The page number.
     * @param pageSize   The page size.
     * @param sortBy     The field to sort by.
     * @param sortDir    The sorting direction.
     * @return A list of TechnicianDto objects.
     */
    @Override
    public List<TechnicianDto> getUsers(int pageNumber, int pageSize, String sortBy, String sortDir) {
        List<TechnicianDto> nlst = new ArrayList<>();
       try{ Sort sort = null;
        if (sortDir.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }
        Pageable p = PageRequest.of(pageNumber, pageSize, sort);
        Page<Technician> pageUser = technicianRepository.findAll(p);
        List<Technician> lst = pageUser.getContent();

        for (Technician technician : lst) {
            TechnicianDto technicianDto = technicianMapper.toDto(technician);
            nlst.add(technicianDto);
        }}
       catch(Exception e){
           throw new DBOperationException(e.getMessage(), e);
       }
        return nlst;
    }

    /**
     * Retrieves a technician by ID.
     *
     * @param id The ID of the technician.
     * @return The TechnicianDto object.
     */
    @Override
    public TechnicianDto getbyId(Integer id) {
        if (id == null) {
            throw new ResourceNotFoundException("ID cannot be null");
        }
        try{
        Optional<Technician> technicianEntityOptional = technicianRepository.findById(id);
        if (technicianEntityOptional.isPresent()) {
            TechnicianDto technicianDto = technicianMapper.toDto(technicianEntityOptional.get());
            return technicianDto;
        } else {
            throw new ResourceNotFoundException("Technician with ID : " + id + " doesn't exist");
        }}
        catch(Exception e){
            throw  new DBOperationException(e.getMessage(),e);
        }
    }

    /**
     * Deletes a technician by ID.
     *
     * @param id The ID of the technician to delete.
     */
    @Override
    public void deletebyId(Integer id) {
        try {
            technicianRepository.deleteById(id);
            log.info("Technician Deleted Successfully");
        } catch (Exception e) {
            throw  new DBOperationException(e.getMessage(),e);
        }
    }


    /**
     * Updates a Technician entity.
     *
     * @param technicianDto The TechnicianDto containing the updated data.
     * @param id            The ID of the Technician to update.
     * @return A BasicRestResponse indicating the status of the operation.
     * @throws DBOperationException if the technician ID is not provided or if an error occurs during the operation.
     */
    @Override
    public BasicRestResponse updateTechnician(TechnicianDto technicianDto, Integer id) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        try {
            if (null == technicianDto.getId()) {
                throw new ResourceNotFoundException("Technician Id not provided for update request");
            }
            Optional<Technician> optionalTechnician = technicianRepository.findById(id);

            Technician technician = optionalTechnician.get();
            performSave(technicianDto,technician, technicianDto.getId());
            response.setMessage("Technician with the given technician id updated successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            return response;
        } catch (Exception e) {
            log.info("Error occurred while updating Technician");
            throw new DBOperationException(e.getMessage(), e);
        }

    }

    /**
     * Helper method to perform the save operation for a Technician entity.
     *
     * @param technicianDto The TechnicianDto containing the data.
     * @param technician    The Technician entity to be updated.
     * @param id            The ID of the Technician to update.
     * @throws DBOperationException if an error occurs during the save operation.
     */
    public void performSave(TechnicianDto technicianDto,Technician technician, Integer id) {
        try {
            if (null == id) {
                technicianMapper.toEntity(technicianDto, technician);

                technicianRepository.save(technician);
            } else {
                    technicianMapper.toEntity(technicianDto, technician);

                    technicianRepository.save(technician);

            }
        } catch (Exception e) {
            log.info("Error occurred during performSave() function");
            throw new DBOperationException(e.getMessage(), e);
        }
    }
}

