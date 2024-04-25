package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.WorkOrderMapper;
import com.marinamooringmanagement.model.dto.WorkOrderDto;
import com.marinamooringmanagement.model.entity.WorkOrder;
import com.marinamooringmanagement.model.request.WorkOrderRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.WorkOrderResponseDto;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public BasicRestResponse saveWorkOrder(final WorkOrderRequestDto workOrderRequestDto) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        try {
            log.info(String.format("Saving data in the database for WorkOrder ID %d", workOrderRequestDto.getId()));
            final WorkOrder workOrder = new WorkOrder();
            performSave(workOrderRequestDto, workOrder, null);
            response.setMessage("Work Order saved successfully");
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            log.error(String.format("Exception occurred while performing save operation: %s", e.getMessage()), e);
            throw new DBOperationException(e.getMessage(), e);
        }

        return response;
    }

    @Override
    public BasicRestResponse getWorkOrders(final Integer pageNumber, final Integer pageSize, final String sortBy, final String sortDir) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info(String.format("Work Orders fetched successfully"));
            final Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

            final Pageable p = PageRequest.of(pageNumber, pageSize, sort);
            final Page<WorkOrder> workOrderList = workOrderRepository.findAll(p);
            List<WorkOrderResponseDto> workOrderResponseDtoList = workOrderList.stream()
                    .map(workOrder -> workOrderMapper.mapToWorkResponseDto(WorkOrderResponseDto.builder().build(), workOrder))
                    .collect(Collectors.toList());
            response.setMessage("List of work orders in the database");
            response.setContent(workOrderResponseDtoList);
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            log.error(String.format("Error occurred while fetching WorkOrders: %s", e.getMessage()), e);

            response.setMessage("Error occurred while fetching list of work orders from the database");
            response.setContent(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
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

    /**
     * Retrieves a Work Order DTO by its ID.
     *
     * @param id The ID of the Work Order to retrieve.
     * @return The corresponding WorkOrderDto.
     * @throws ResourceNotFoundException If the Work Order with the given ID is not found.
     * @throws DBOperationException      If an error occurs during database operations.
     */
    @Override
    public BasicRestResponse getbyId(final Integer id) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        if (id == null) {
            throw new ResourceNotFoundException("ID cannot be null");
        }
        try {
            Optional<WorkOrder> workOrderEntityOptional = workOrderRepository.findById(id);
            if (workOrderEntityOptional.isPresent()) {
                log.info(String.format("Successfully retrieved WorkOrder data for ID: %d", id));

                WorkOrderDto workOrderDto = workOrderMapper.toDto(workOrderEntityOptional.get());
                response.setMessage("List of work order by id from the database");
                response.setContent(workOrderDto);
                response.setStatus(HttpStatus.OK.value());
            } else {
                throw new ResourceNotFoundException("Work Order with ID : " + id + " doesn't exist");
            }
        } catch (Exception e) {
            log.error(String.format("Error occurred while retrieving Work Order for ID: %d: %s", id, e.getMessage()), e);
            throw new DBOperationException(e.getMessage(), e);
        }
        return response;
    }

    /**
     * Deletes a Work Order by its ID.
     *
     * @param id The ID of the Work Order to delete.
     * @return A BasicRestResponse indicating the status of the deletion operation.
     * @throws DBOperationException If an error occurs during database operations.
     */
    @Override
    public BasicRestResponse deleteWorkOrderbyId(final Integer id) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setMessage("Deleted work order");
        response.setStatus(HttpStatus.OK.value());

        try {
            workOrderRepository.deleteById(id);
            log.info(String.format("Work Order with ID %d deleted successfully", id));

        } catch (Exception e) {
            log.error(String.format("Error occurred while deleting work order with ID %d", id));

            throw new DBOperationException(e.getMessage(), e);
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
    public BasicRestResponse updateWorkOrder(final WorkOrderRequestDto workOrderRequestDto, final Integer id) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        try {
            if (null == workOrderRequestDto.getId()) {
                log.info(String.format("Update attempt without a Work Order ID provided in the request DTO"));
                throw new ResourceNotFoundException("Work Order Id not provided for update request");
            }
            Optional<WorkOrder> optionalWorkOrder = workOrderRepository.findById(id);
            if (optionalWorkOrder.isPresent()) {

                WorkOrder workOrder = optionalWorkOrder.get();
                performSave(workOrderRequestDto, workOrder, workOrderRequestDto.getId());
                response.setMessage("Work Order with the given work order id updated successfully!!!");
                response.setStatus(HttpStatus.OK.value());

            }
        } catch (Exception e) {
            log.error(String.format("Error occurred while updating work order: %s", e.getMessage()), e);

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
    public void performSave(final WorkOrderRequestDto workOrderRequestDto, final WorkOrder workOrder, final Integer id) {
        try {
            if (null == id) {
                workOrder.setLastModifiedDate(new Date(System.currentTimeMillis()));
            }
            workOrderMapper.mapToWorkOrder(workOrder, workOrderRequestDto);
            workOrder.setCreationDate(new Date());
            workOrder.setLastModifiedDate(new Date());

            workOrderRepository.save(workOrder);
            log.info(String.format("Work Order saved successfully with ID: %d", workOrder.getId()));

        } catch (Exception e) {
            log.error(String.format("Error occurred during performSave() function: %s", e.getMessage()), e);
            throw new DBOperationException(e.getMessage(), e);
        }
    }


}
