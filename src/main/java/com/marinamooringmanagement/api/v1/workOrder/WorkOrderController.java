package com.marinamooringmanagement.api.v1.workOrder;

import com.marinamooringmanagement.exception.handler.GlobalExceptionHandler;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.WorkOrderRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.WorkOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping("/api/v1/workOrder")
@Validated
@CrossOrigin
public class WorkOrderController extends GlobalExceptionHandler {

    @Autowired
    private WorkOrderService workOrderService;

    /**
     * Fetches a list of moorings based on pagination and sorting parameters.
     *
     * @param pageNumber    Page number for pagination (default: 1)
     * @param pageSize    Page size for pagination (default: 10)
     * @param sortBy  Field to sort by (default: "id")
     * @param sortDir Sorting direction (asc/desc, default: "asc")
     * @return BasicRestResponse containing the fetched moorings
     */
    @Operation(
            tags = "Fetch work orders from the database",
            description = "API to fetch work orders from the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "400"
                    )
            }

    )
    @RequestMapping(
            value = "/",
            method = RequestMethod.GET,
            produces = {"application/json"}
    )
    public BasicRestResponse fetchWorkOrders(
            @Parameter(description = "Page Number", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            @Parameter(description = "Page Size", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @Parameter(description = "Sort By(field)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @Parameter(description = "Sort Dir(asc --> ascending or des --> descending)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            @Parameter(description = "Search Text", schema = @Schema(implementation = String.class)) final @RequestParam(value = "searchText", required = false) String searchText,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
        return workOrderService.fetchWorkOrders(baseSearchRequest, searchText, request);
    }

    @Operation(
            tags = "Fetch open work orders from the database",
            description = "API to fetch open work orders from the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "400"
                    )
            }

    )
    @RequestMapping(
            value = "/fetchOpenWorkOrders/{technicianId}",
            method = RequestMethod.GET,
            produces = {"application/json"}
    )
    public BasicRestResponse fetchOpenWorkOrders(
            @Parameter(description = "TechnicianId", schema = @Schema(implementation = Integer.class)) final @PathVariable("technicianId") Integer technicianId,
            @Parameter(description = "Page Number", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            @Parameter(description = "Page Size", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @Parameter(description = "Sort By(field)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @Parameter(description = "Sort Dir(asc --> ascending or des --> descending)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            @Parameter(description = "Filter date from", schema = @Schema(implementation = String.class)) final @RequestParam(value = "filterDateFrom", required = false) String filterDateFrom,
            @Parameter(description = "Filter date to", schema = @Schema(implementation = String.class)) final @RequestParam(value = "filterDateTo", required = false) String filterDateTo,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
        return workOrderService.fetchOpenWorkOrders(baseSearchRequest, technicianId, request, filterDateFrom, filterDateTo);
    }

    @Operation(
            tags = "Fetch all open work orders from the database",
            description = "API to fetch all open work orders from the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "400"
                    )
            }

    )
    @RequestMapping(
            value = "/fetchAllOpenWorkOrders",
            method = RequestMethod.GET,
            produces = {"application/json"}
    )
    @Transactional
    public BasicRestResponse fetchAllOpenWorkOrders(
            @Parameter(description = "Page Number", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            @Parameter(description = "Page Size", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @Parameter(description = "Sort By(field)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @Parameter(description = "Sort Dir(asc --> ascending or des --> descending)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            @Parameter(description = "Filter date from", schema = @Schema(implementation = String.class)) final @RequestParam(value = "filterDateFrom", required = false) String filterDateFrom,
            @Parameter(description = "Filter date to", schema = @Schema(implementation = String.class)) final @RequestParam(value = "filterDateTo", required = false) String filterDateTo,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
        return workOrderService.fetchAllOpenWorkOrders(baseSearchRequest, request, filterDateFrom, filterDateTo);
    }

    @Operation(
            tags = "Fetch closed work orders from the database",
            description = "API to fetch closed work orders from the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "400"
                    )
            }

    )
    @RequestMapping(
            value = "/fetchCloseWorkOrders/{technicianId}",
            method = RequestMethod.GET,
            produces = {"application/json"}
    )
    public BasicRestResponse fetchCloseWorkOrders(
            @Parameter(description = "TechnicianId", schema = @Schema(implementation = Integer.class)) final @PathVariable("technicianId") Integer technicianId,
            @Parameter(description = "Page Number", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            @Parameter(description = "Page Size", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @Parameter(description = "Sort By(field)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @Parameter(description = "Sort Dir(asc --> ascending or des --> descending)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            @Parameter(description = "Filter date from", schema = @Schema(implementation = String.class)) final @RequestParam(value = "filterDateFrom", required = false) String filterDateFrom,
            @Parameter(description = "Filter date to", schema = @Schema(implementation = String.class)) final @RequestParam(value = "filterDateTo", required = false) String filterDateTo,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
        return workOrderService.fetchCloseWorkOrders(baseSearchRequest, technicianId, request, filterDateFrom, filterDateTo);
    }

    /**
     * Saves a new work order.
     *
     * @param workOrderRequestDto Request DTO containing work order details
     * @return BasicRestResponse indicating the status of the operation
     */
    @Operation(
            tags = "Save work orders in the database",
            description = "API to save work orders in the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "400"
                    )
            }

    )
    @RequestMapping(value = "/",
            method = RequestMethod.POST,
            produces = {"application/json"})
    public BasicRestResponse saveWorkOrder(
            @Parameter(description = "Properties of a work order", schema = @Schema(implementation = WorkOrderRequestDto.class)) final @Valid @RequestBody WorkOrderRequestDto  workOrderRequestDto,
            final HttpServletRequest request
    ) {
        return workOrderService.saveWorkOrder(workOrderRequestDto, request);
    }

    /**
     * Updates an existing work order.
     *
     * @param workOrderRequestDto Request DTO containing updated work order details
     * @param workOrderId ID of the work order to be updated
     * @return BasicRestResponse indicating the status of the operation
     */
    @Operation(
            tags = "Update work order in the database",
            description = "API to update work order in the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "400"
                    )
            }

    )
    @RequestMapping(value = "/{id}",
            method = RequestMethod.PUT,
            produces = {"application/json"})
    public BasicRestResponse updateMooring(
            @Parameter(description = "Fields to update in the work order", schema = @Schema(implementation = WorkOrderRequestDto.class)) final @Valid @RequestBody WorkOrderRequestDto workOrderRequestDto,
            @Parameter(description = "ID of the work order to be updated", schema = @Schema(implementation = Integer.class)) final @PathVariable("id") Integer workOrderId,
            final HttpServletRequest request
    ) {
        return workOrderService.updateWorkOrder(workOrderRequestDto, workOrderId, request);
    }

    /**
     * Deletes a work order by ID.
     *
     * @param id ID of the work order to be deleted
     * @return BasicRestResponse indicating the status of the deletion operation
     */
    @Operation(
            tags = "Delete work order from the database",
            description = "API to delete work order from the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "400"
                    )
            }

    )
    @RequestMapping(value = "/{id}",
            method = RequestMethod.DELETE,
            produces = {"application/json"})
    public BasicRestResponse deleteMooring(
            @Parameter(description = "Id of the work order to be deleted", schema = @Schema(implementation = Integer.class)) final @PathVariable("id") Integer id,
            final HttpServletRequest request
    ) {
        return workOrderService.deleteWorkOrder(id, request);
    }

}
