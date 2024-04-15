package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.BoatYardMapper;
import com.marinamooringmanagement.model.dto.BoatYardDto;
import com.marinamooringmanagement.model.entity.BoatYard;
import com.marinamooringmanagement.model.request.BoatYardRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.repositories.BoatYardRepository;
import com.marinamooringmanagement.service.BoatYardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


/**
 * Implementation of the BoatYardService interface.
 * Provides methods for CRUD operations on BoatYard entities.
 */
@Service
public class BoatYardServiceImpl implements BoatYardService {

    @Autowired
    private BoatYardRepository boatYardRepository;

    @Autowired
    private BoatYardMapper boatYardMapper;

    private static final Logger log = LoggerFactory.getLogger(BoatYardServiceImpl.class);

    /**
     * Saves a BoatYard entity.
     *
     * @param boatYardRequestDto The BoatYardDto containing the data to be saved.
     */
    @Override
    public BasicRestResponse saveBoatYard(BoatYardRequestDto boatYardRequestDto) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));


         try {
            BoatYard boatYard = new BoatYard();
             performSave(boatYardRequestDto,boatYard,null);
            response.setMessage("BoatYard Saved Successfully");
            response.setStatus(HttpStatus.CREATED.value());
            log.info("BoatYard saved Successfully");
        } catch (Exception e) {
            log.error("Exception occurred while performing save operation " + e);
            throw new DBOperationException(e.getMessage(), e);
        }
return response;
    }

    /**
     * Retrieves a list of BoatYard entities.
     *
     * @param pageNumber The page number.
     * @param pageSize   The size of each page.
     * @param sortBy     The field to sort by.
     * @param sortDir    The direction of sorting.
     * @return A list of BoatYardDto objects.
     */
    @Override
    public List<BoatYardDto> getBoatYard(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        List<BoatYardDto> nlst = new ArrayList<>();
        try{
        Sort sort = null;
        if(sortDir.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }
        Pageable p = PageRequest.of(pageNumber, pageSize, sort);
        Page<BoatYard> pageUser = boatYardRepository.findAll(p);
        List<BoatYard> lst = pageUser.getContent();

        for(BoatYard boatYard: lst) {
            BoatYardDto boatYardDto = boatYardMapper.toDto(boatYard);
            nlst.add(boatYardDto);
        }}
        catch(Exception e){
            throw new DBOperationException(e.getMessage(), e);
        }

        return nlst;
    }
    /**
     * Retrieves a BoatYard entity by its ID.
     *
     * @param id The ID of the BoatYard to retrieve.
     * @return The BoatYardDto object corresponding to the given ID.
     */
    @Override
    public BoatYardDto getbyId(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID cannot be null");
        }
        try {
            Optional<BoatYard> BoatYardEntityOptional = boatYardRepository.findById(id);
            if (BoatYardEntityOptional.isPresent()) {
                BoatYardDto boatYardDto = boatYardMapper.toDto(BoatYardEntityOptional.get());
                return boatYardDto;
            } else {
                throw new DBOperationException("BoatYard with ID : " + id + " doesn't exist");
            }
        } catch (Exception e) {
            throw new DBOperationException(e.getMessage(), e);

        }
    }

    /**
     * Deletes a BoatYard entity by its ID.
     *
     * @param id The ID of the BoatYard to delete.
     */
    @Override
    public BasicRestResponse deleteBoatYardbyId(Integer id) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            boatYardRepository.deleteById(id);
            response.setMessage("Boatyard deleted");
            response.setStatus(HttpStatus.OK.value());
            log.info("BoatYard Deleted Successfully");
        } catch (Exception e) {
            throw new DBOperationException(e.getMessage(), e);
        }
        return  response;
    }

    /**
     * Updates a BoatYard entity.
     *
     * @param boatYardRequestDto The BoatYardDto containing the updated data.
     * @param id          The ID of the BoatYard to update.
     * @return A BasicRestResponse indicating the status of the operation.
     */
    @Override
    public BasicRestResponse updateBoatYard(BoatYardRequestDto boatYardRequestDto, Integer id) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        try {
            if (null == boatYardRequestDto.getId()) {

                throw new ResourceNotFoundException("BoatYard Id not provided for update request");
            }
            Optional<BoatYard> optionalBoatYard = boatYardRepository.findById(id);
            BoatYard boatYard = optionalBoatYard.get();
            performSave(boatYardRequestDto,boatYard, boatYardRequestDto.getId());
            response.setMessage("BoatYard with the given boatyard id updated successfully!!!");
            response.setStatus(HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error occurred while updating boatyard");
            throw new DBOperationException(e.getMessage(), e);
        }
        return response;
    }

    /**
     * Helper method to perform the save operation for a BoatYard entity.
     *
     * @param boatYardRequestDto The BoatYardDto containing the data.
     * @param boatYard    The BoatYard entity to be updated.
     * @param id          The ID of the BoatYard to update.
     */
    public void performSave(BoatYardRequestDto boatYardRequestDto,BoatYard boatYard, Integer id) {

        try {
            if (null == id) {

                boatYard.setLastModifiedDate(new Date(System.currentTimeMillis()));

            }

                    boatYardMapper.mapToBoatYard(boatYard,boatYardRequestDto);
                boatYard.setCreationDate(new Date());
                boatYard.setLastModifiedDate(new Date());

                    boatYardRepository.save(boatYard);


        } catch (Exception e) {
            log.info("Error occurred during performSave() function");
            throw new DBOperationException(e.getMessage(), e);
        }
    }
}
