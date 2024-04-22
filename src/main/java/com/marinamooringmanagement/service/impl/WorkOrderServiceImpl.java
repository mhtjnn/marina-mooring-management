package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.WorkOrderMapper;
import com.marinamooringmanagement.model.dto.WorkOrderDto;
import com.marinamooringmanagement.model.entity.WorkOrder;
import com.marinamooringmanagement.model.request.WorkOrderRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.repositories.WorkOrderRepository;
import com.marinamooringmanagement.service.WorkOrderService;
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
 * Implementation of the WorkOrderService interface that handles operations and business logic for Work Orders.
 */
@Service
public class WorkOrderServiceImpl implements WorkOrderService {


    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private WorkOrderMapper workOrderMapper;

    private static final Logger log = LoggerFactory.getLogger(WorkOrderServiceImpl.class);

    /**
     * Saves a new Work Order or updates an existing one.
     *
     * @param workOrderRequestDto The DTO containing Work Order data to be saved or updated.
     * @return A BasicRestResponse indicating the status of the operation.
     * @throws DBOperationException If an error occurs during database operations.
     */
    @Override
    public BasicRestResponse saveWorkOrder(WorkOrderRequestDto workOrderRequestDto) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        try {
            log.info("Save the data in the database");
            WorkOrder workOrder = new WorkOrder();
            performSave(workOrderRequestDto, workOrder, null);
            response.setMessage("Customer saved successfully");
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            log.error("Exception occurred while performing save operation " + e);
            throw new DBOperationException(e.getMessage(), e);
        }

        return response;
    }

    /**
     * Retrieves a list of Work Order DTOs based on pagination and sorting criteria.
     *
     * @param pageNumber The page number for pagination.
     * @param pageSize   The page size for pagination.
     * @param sortBy     The field to sort by.
     * @param sortDir    The sort direction (asc or desc).
     * @return A list of WorkOrderDto objects.
     * @throws DBOperationException If an error occurs during database operations.
     */
    @Override
    public List<WorkOrderDto> getWorkOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
         List<WorkOrderDto> nlst = new ArrayList<>();
            try{ Sort sort = null;
                if (sortDir.equalsIgnoreCase("asc")) {
                    sort = Sort.by(sortBy).ascending();
                } else {
                    sort = Sort.by(sortBy).descending();
                }
                Pageable p = PageRequest.of(pageNumber, pageSize, sort);
                Page<WorkOrder> pageUser = workOrderRepository.findAll(p);
                List<WorkOrder> lst = pageUser.getContent();

                for (WorkOrder workOrder : lst) {
                    WorkOrderDto workOrderDto = workOrderMapper.toDto(workOrder);
                    nlst.add(workOrderDto);
                }}
            catch(Exception e){
                throw new DBOperationException(e.getMessage(), e);
            }
            return nlst;
        }

    /**
     * Retrieves a Work Order DTO by its ID.
     *
     * @param id The ID of the Work Order to retrieve.
     * @return The corresponding WorkOrderDto.
     * @throws ResourceNotFoundException If the Work Order with the given ID is not found.
     * @throws DBOperationException      If an error occurs during database operations.
     */
    @Override
    public WorkOrderDto getbyId(Integer id) {
        if (id == null) {
            throw new ResourceNotFoundException("ID cannot be null");
        }
        try{
            Optional<WorkOrder> workOrderEntityOptional = workOrderRepository.findById(id);
            if (workOrderEntityOptional.isPresent()) {
                WorkOrderDto workOrderDto = workOrderMapper.toDto(workOrderEntityOptional.get());
                return workOrderDto;
            } else {
                throw new ResourceNotFoundException("Work Order with ID : " + id + " doesn't exist");
            }}
        catch(Exception e){
            throw  new DBOperationException(e.getMessage(),e);
        }
    }

    /**
     * Deletes a Work Order by its ID.
     *
     * @param id The ID of the Work Order to delete.
     * @return A BasicRestResponse indicating the status of the deletion operation.
     * @throws DBOperationException If an error occurs during database operations.
     */
    @Override
    public BasicRestResponse deleteWorkOrderbyId(Integer id) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setMessage("Deleted work order");
        response.setStatus(HttpStatus.OK.value());
        try {
            workOrderRepository.deleteById(id);
            log.info("Work Order Deleted Successfully");
        } catch (Exception e) {
            throw  new DBOperationException(e.getMessage(),e);
        }
        return response;
    }

    /**
     * Updates an existing Work Order with new data.
     *
     * @param workOrderRequestDto The DTO containing the updated Work Order data.
     * @param id                  The ID of the Work Order to update.
     * @return A BasicRestResponse indicating the status of the update operation.
     * @throws ResourceNotFoundException If the Work Order with the given ID is not found.
     * @throws DBOperationException      If an error occurs during database operations.
     */
    @Override
    public BasicRestResponse updateWorkOrder(WorkOrderRequestDto workOrderRequestDto, Integer id) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        try {
            if (null == workOrderRequestDto.getId()) {
                throw new ResourceNotFoundException("Work Order Id not provided for update request");
            }
            Optional<WorkOrder> optionalWorkOrder = workOrderRepository.findById(id);

            WorkOrder workOrder = optionalWorkOrder.get();
            performSave(workOrderRequestDto,workOrder, workOrderRequestDto.getId());
            response.setMessage("Work Order with the given work order id updated successfully!!!");
            response.setStatus(HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error occurred while updating Work Order");
            throw new DBOperationException(e.getMessage(), e);
        }
        return response;
    }

    /**
     * Helper method to perform the save operation for a WorkOrder entity.
     *
     * @param workOrderRequestDto The DTO containing Work Order data to be saved.
     * @param workOrder           The WorkOrder entity to be updated or created.
     * @param id                  The ID of the WorkOrder entity for update operations.
     * @throws DBOperationException If an error occurs during database operations.
     */
    public void performSave(WorkOrderRequestDto workOrderRequestDto, WorkOrder workOrder, Integer id) {
        try {
            if (null == id) {
                workOrder.setLastModifiedDate(new Date(System.currentTimeMillis()));
            }
            workOrderMapper.mapToWorkOrder(workOrder,workOrderRequestDto);
            workOrder.setCreationDate(new Date());
            workOrder.setLastModifiedDate(new Date());

            workOrderRepository.save(workOrder);
        } catch (Exception e) {
            log.info("Error occurred during performSave() function");
            throw new DBOperationException(e.getMessage(), e);
        }
    }




}
