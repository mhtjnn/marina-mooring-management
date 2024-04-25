package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.WorkOrderRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;

/**
 * Service interface for handling Work Order operations.
 */
public interface WorkOrderService {

    /**
     * Saves a new Work Order or updates an existing one.
     *
     * @param workOrderRequestDto The DTO containing Work Order data to be saved or updated.
     * @return A BasicRestResponse indicating the status of the operation.
     */
    BasicRestResponse saveWorkOrder(final WorkOrderRequestDto workOrderRequestDto);

    /**
     * Retrieves a list of Work Order DTOs based on pagination and sorting criteria.
     *
     * @param pageNumber The page number for pagination.
     * @param pageSize   The page size for pagination.
     * @param sortBy     The field to sort by.
     * @param sortDir    The sort direction (asc or desc).
     * @return A list of WorkOrderDto objects.
     */
    BasicRestResponse getWorkOrders(final Integer pageNumber, final Integer pageSize, final String sortBy, final String sortDir);

    /**
     * Retrieves a Work Order DTO by its ID.
     *
     * @param id The ID of the Work Order to retrieve.
     * @return The corresponding WorkOrderDto.
     */
    BasicRestResponse getbyId(final Integer id);

    /**
     * Deletes a Work Order by its ID.
     *
     * @param id The ID of the Work Order to delete.
     * @return A BasicRestResponse indicating the status of the deletion operation.
     */
    BasicRestResponse deleteWorkOrderbyId(final Integer id);

    /**
     * Updates an existing Work Order with new data.
     *
     * @param workOrderRequestDto The DTO containing the updated Work Order data.
     * @param id                  The ID of the Work Order to update.
     * @return A BasicRestResponse indicating the status of the update operation.
     */
    BasicRestResponse updateWorkOrder(final WorkOrderRequestDto workOrderRequestDto, final Integer id);
}
