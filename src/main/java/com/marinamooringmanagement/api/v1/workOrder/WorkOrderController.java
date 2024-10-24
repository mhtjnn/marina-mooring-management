package com.marinamooringmanagement.api.v1.workOrder;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.constants.Authority;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static com.marinamooringmanagement.constants.AppConstants.BooleanStringConst.NO;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping("/api/v1/workOrder")
@Validated
@CrossOrigin
@Tag(name = "Work order Controller", description = "These are API's for work order.")
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
            summary = "API to fetch work orders from the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER + " or " + Authority.TECHNICIAN)
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
            @Parameter(description = "Show completed work orders or not", schema = @Schema(implementation = String.class)) final @RequestParam(value = "showCompletedWorkOrders", defaultValue = NO) String showCompletedWorkOrders,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
        return workOrderService.fetchWorkOrders(baseSearchRequest, searchText, showCompletedWorkOrders, request);
    }

    @Operation(
            summary =  "API to fetch completed work orders with pay status from the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER + " or " + Authority.FINANCE)
    @RequestMapping(
            value = "/fetchCompletedWorkOrdersWithPayStatus",
            method = RequestMethod.GET,
            produces = {"application/json"}
    )
    public BasicRestResponse fetchCompletedWorkOrdersWithPayStatus(
            @Parameter(description = "Page Number", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            @Parameter(description = "Page Size", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @Parameter(description = "Sort By(field)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @Parameter(description = "Sort Dir(asc --> ascending or des --> descending)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            @Parameter(description = "Search Text", schema = @Schema(implementation = String.class)) final @RequestParam(value = "searchText", required = false) String searchText,
            @Parameter(description = "Pay status", schema = @Schema(implementation = String.class)) final @RequestParam(value = "payStatus", defaultValue = AppConstants.WorkOrderPayStatusConstants.NOACTION) String payStatus,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
        return workOrderService.fetchCompletedWorkOrdersWithPendingPayApproval(baseSearchRequest, searchText, request, payStatus);
    }

    @Operation(
            summary = "API to fetch all work order invoices from the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER + " or " + Authority.FINANCE)
    @RequestMapping(
            value = "/fetchWorkOrderInvoice",
            method = RequestMethod.GET,
            produces = {"application/json"}
    )
    public BasicRestResponse fetchWorkOrderInvoice(
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
        return workOrderService.fetchWorkOrderInvoice(baseSearchRequest, searchText, request);
    }

    @Operation(
            summary = "API to fetch open work orders from the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
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
            summary = "API to fetch all open work orders and mooring due for service from the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    @RequestMapping(
            value = "/fetchAllOpenWorkOrdersAndMooringDueForService",
            method = RequestMethod.GET,
            produces = {"application/json"}
    )
    @Transactional
    public BasicRestResponse fetchAllOpenWorkOrdersAndMooringDueForService(
            @Parameter(description = "Page Number", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            @Parameter(description = "Page Size", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @Parameter(description = "Sort By(field)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @Parameter(description = "Sort Dir(asc --> ascending or des --> descending)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
        return workOrderService.fetchAllOpenWorkOrdersAndMooringDueForService(baseSearchRequest, request);
    }

    @Operation(
            summary = "API to fetch closed work orders from the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
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
            summary = "API to save work orders in the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER + " or " + Authority.TECHNICIAN)
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
            summary = "API to update work order in the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER + " or " + Authority.TECHNICIAN)
    @RequestMapping(value = "/{id}",
            method = RequestMethod.PUT,
            produces = {"application/json"})
    public BasicRestResponse updateWorkOrder(
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
            summary = "API to delete work order from the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    @RequestMapping(value = "/{id}",
            method = RequestMethod.DELETE,
            produces = {"application/json"})
    public BasicRestResponse deleteWorkOrder(
            @Parameter(description = "Id of the work order to be deleted", schema = @Schema(implementation = Integer.class)) final @PathVariable("id") Integer id,
            final HttpServletRequest request
    ) {
        return workOrderService.deleteWorkOrder(id, request);
    }

    @Operation(
            summary = "API to approve work order pay status from the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER + " or " + Authority.FINANCE)
    @RequestMapping(value = "/approveWorkOrder/{id}",
            method = RequestMethod.PUT,
            produces = {"application/json"})
    public BasicRestResponse approveWorkOrder(
            @Parameter(description = "Id of the work order to be deleted", schema = @Schema(implementation = Integer.class)) final @PathVariable("id") Integer id,
            @Parameter(description = "Invoice amount", schema = @Schema(implementation = Double.class)) final @RequestParam("invoiceAmount") BigDecimal invoiceAmount,
            final HttpServletRequest request
    ) {
        return workOrderService.approveWorkOrder(id, request, invoiceAmount);
    }

    @Operation(
            summary = "API to deny work order pay status from the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER + " or " + Authority.FINANCE)
    @RequestMapping(value = "/denyWorkOrder/{id}",
            method = RequestMethod.PUT,
            produces = {"application/json"})
    public BasicRestResponse denyWorkOrder(
            @Parameter(description = "Id of the work order to be deleted", schema = @Schema(implementation = Integer.class)) final @PathVariable("id") Integer id,
            @Parameter(description = "Report the problem for the denial of the work order.", schema = @Schema(implementation = String.class)) final @RequestParam("reportProblem") String reportProblem,
            final HttpServletRequest request
    ) {
        return workOrderService.denyWorkOrder(id, request, reportProblem);
    }

    @Operation(
            summary = "API to fetch mooring due for service for a particular technician from the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER + " or " + Authority.TECHNICIAN)
    @RequestMapping(value = "/mooringsDueForService",
            method = RequestMethod.GET,
            produces = {"application/json"})
    public BasicRestResponse fetchMooringDueForServiceForTechnician(
    ) {
        return workOrderService.fetchMooringDueForServiceForTechnician();
    }

    @Operation(
            summary = "API to fetch work order of a given id from the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER + " or " + Authority.TECHNICIAN)
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET,
            produces = {"application/json"}
    )
    public BasicRestResponse fetchWorkOrderById(
            @Parameter(description = "Work Order Id", schema = @Schema(implementation = Integer.class)) @PathVariable("id") final Integer id,
            final HttpServletRequest request
    ) {
        return workOrderService.fetchWorkOrderById(id, request);
    }
}
