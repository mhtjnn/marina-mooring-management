package com.marinamooringmanagement.api.v1.workorder;


import com.marinamooringmanagement.model.request.WorkOrderRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.WorkOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE;

/**
 * Controller class for handling WorkOrder-related API endpoints.
 */
@RestController
@Validated
@RequestMapping("api/v1/workorder")
@Tag(name="WorkOrderController",description = "To perform operations on Work Order")
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    /**
     * Endpoint for saving a new WorkOrder.
     *
     * @param workOrderRequestDto The request body containing WorkOrder data to be saved.
     * @return BasicRestResponse indicating the status of the operation.
     */

    @PostMapping(value = "/",
            produces = {"application/json"})
    public BasicRestResponse saveWorkOrder(@Valid @RequestBody WorkOrderRequestDto workOrderRequestDto
    ) {

        return workOrderService.saveWorkOrder(workOrderRequestDto);
    }

    /**
     * Endpoint for retrieving a list of WorkOrders with pagination and sorting options.
     *
     * @param pageNumber The page number for pagination.
     * @param pageSize   The page size for pagination.
     * @param sortBy     The field to sort by.
     * @param sortDir    The sort direction ('asc' for ascending, 'desc' for descending).
     * @return List of WorkOrderDto objects.
     */

    @GetMapping(value = "/",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse getWorkOrders(
            @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        return workOrderService.getWorkOrders(pageNumber, pageSize, sortBy, sortDir);
    }

    /**
     * Endpoint for retrieving a single WorkOrder by its ID.
     *
     * @param id The ID of the WorkOrder to retrieve.
     * @return WorkOrderDto object.
     */

    @GetMapping(value = "/{id}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse getWorkOrder(@PathVariable(value = "id") Integer id) {
        return this.workOrderService.getbyId(id);
    }

    /**
     * Endpoint for deleting a WorkOrder by its ID.
     *
     * @param id The ID of the WorkOrder to delete.
     * @return BasicRestResponse indicating the status of the operation.
     */

    @DeleteMapping(value = "/{id}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse deleteWorkOrder(@PathVariable(value = "id") Integer id) {

        return workOrderService.deleteWorkOrderbyId(id);
    }

    /**
     * Endpoint for updating a WorkOrder by its ID.
     *
     * @param id                  The ID of the WorkOrder to update.
     * @param workOrderRequestDto The request body containing updated WorkOrder data.
     * @return BasicRestResponse indicating the status of the operation.
     */

    @PutMapping(value = "/{id}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse updateWorkOrder(
            @PathVariable(value = "id",required = true) Integer id,
            @Valid @RequestBody WorkOrderRequestDto workOrderRequestDto
    ){

        return  workOrderService.updateWorkOrder(workOrderRequestDto,id);
    }
}
