package com.marinamooringmanagement.api.v1.quickbook;

import com.marinamooringmanagement.constants.Authority;
import com.marinamooringmanagement.exception.handler.GlobalExceptionHandler;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.QuickbookCustomerRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.QuickbookCustomerResponseDto;
import com.marinamooringmanagement.service.QuickbookCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping(value = "/api/v1/quickbook")
@CrossOrigin
@Validated
@Tag(name = "Quickbook customer Controller", description = "These are API's for quickbook customer.")
public class QuickbookCustomerController extends GlobalExceptionHandler {

    @Autowired
    private QuickbookCustomerService quickbookCustomerService;

    /**
     * Fetches a paginated list of quickbook from the database.
     * <p>
     * This REST API endpoint retrieves all quickbook, supports pagination, and allows sorting by specified attributes.
     *
     * @param pageNumber      Page number for pagination, default is 0.
     * @param pageSize        Number of records per page, default is 20.
     * @param sortBy          Attribute name to sort the quickbook, default is "id".
     * @param sortDir         Direction of sorting, can be either "asc" for ascending or "desc" for descending, default is "asc".
     * @param searchText      Optional parameter for searching quickbook by text.
     * @return A {@link BasicRestResponse} containing a list of {@link QuickbookCustomerResponseDto} representing the quickbook.
     */
    @Operation(
            summary = "API to fetch quickbooks from the database",
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
            method = RequestMethod.GET
    )
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "auth")
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse fetchQuickbookCustomers(
            @Parameter(description = "Page Number", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            @Parameter(description = "Page Size", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @Parameter(description = "Sort By(field to be compared for sorting)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @Parameter(description = "Sort Direction(asc --> ascending and dsc --> descending)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            @RequestParam(value = "searchText", required = false) final String searchText,
            final HttpServletRequest request
    ) {
        BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
        return quickbookCustomerService.fetchQuickbookCustomers(baseSearchRequest, searchText, request);
    }

    /**
     * Saves a new quickbook customer in the database.
     * <p>
     * This endpoint is used to create a new quickbook customer with the details provided in the request body.
     *
     * @param quickbookCustomerRequestDto {@link QuickbookCustomerRequestDto} containing the quickbook customer details to be saved.
     * @return A {@link BasicRestResponse} indicating the outcome of the save operation.
     */
    @Operation(
            summary = "API to save quickbook customer in the database",
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
    @SecurityRequirement(name = "auth")
    @RequestMapping(
            value = "/",
            method = RequestMethod.POST,
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse saveQuickbookCustomer(
            @Parameter(description = "QuickbookCustomer to save", schema = @Schema(implementation = QuickbookCustomerRequestDto.class)) final @Valid @RequestBody QuickbookCustomerRequestDto quickbookCustomerRequestDto,
            final HttpServletRequest request
    ) {
        return quickbookCustomerService.saveQuickbookCustomer(quickbookCustomerRequestDto, request);
    }

    /**
     * Updates an existing quickbook customer in the database.
     * <p>
     * This endpoint allows updating quickbook customer details for an existing quickbook customer identified by the information provided in the request body.
     *
     * @param quickbookCustomerId ID of the quickbook customer to update.
     * @param quickbookCustomerRquestDto {@link QuickbookCustomerRequestDto} containing updated quickbook customer details.
     * @return A {@link BasicRestResponse} indicating the outcome of the update operation.
     */
    @Operation(
            summary = "API to update quickbook customer in the database",
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
    @SecurityRequirement(name = "auth")
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT,
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse updateQuickbookCustomer(
            @Parameter(description = "QuickbookCustomer ID", schema = @Schema(implementation = Integer.class)) final @PathVariable("id") Integer quickbookCustomerId,
            @Parameter(description = "Fields to update in the quickbook customer", schema = @Schema(implementation = QuickbookCustomerRequestDto.class)) final @Valid @RequestBody QuickbookCustomerRequestDto quickbookCustomerRquestDto,
            final HttpServletRequest request
    ) {
        return quickbookCustomerService.updateQuickbookCustomer(quickbookCustomerRquestDto, quickbookCustomerId, request);
    }

    /**
     * Deletes a quickbook customer from the database.
     * <p>
     * This endpoint removes a quickbook customer from the database, identified by the quickbook customer ID provided as a path variable.
     *
     * @param quickbookCustomerId ID of the quickbook customer to be deleted.
     * @return A {@link BasicRestResponse} indicating the outcome of the delete operation.
     */
    @Operation(
            summary = "API to delete quickbook customer from the database",
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
    @SecurityRequirement(name = "auth")
    @RequestMapping(
            value = "/{quickbookCustomerId}",
            method = RequestMethod.DELETE,
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse deleteQuickbookCustomer(
            @Parameter(description = "QuickbookCustomer ID", schema = @Schema(implementation = Integer.class)) final @PathVariable("quickbookCustomerId") Integer quickbookCustomerId,
            final HttpServletRequest request
    ) {
        return quickbookCustomerService.deleteQuickbookCustomer(quickbookCustomerId, request);
    }

    @Operation(
            summary = "API to save mapping customer to quickbook customer from the database",
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
    @SecurityRequirement(name = "auth")
    @RequestMapping(
            value = "/saveMappingCustomerToQuickbook/{quickbookCustomerId}/{customerId}",
            method = RequestMethod.POST,
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse saveMappingCustomerToQuickbook(
            @Parameter(description = "QuickbookCustomer ID", schema = @Schema(implementation = Integer.class)) final @PathVariable("quickbookCustomerId") Integer quickbookCustomerId,
            @Parameter(description = "Customer ID", schema = @Schema(implementation = Integer.class)) final @PathVariable("customerId") Integer customerId,
            final HttpServletRequest request
    ) {
        return quickbookCustomerService.saveMappingCustomerToQuickbook(quickbookCustomerId, customerId, request);
    }

    @Operation(
            summary = "API to edit mapping customer to quickbook customer from the database",
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
    @SecurityRequirement(name = "auth")
    @RequestMapping(
            value = "/editMappingCustomerToQuickbook/{quickbookCustomerId}/{customerId}",
            method = RequestMethod.PUT,
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse editMappingCustomerToQuickbook(
            @Parameter(description = "QuickbookCustomer ID", schema = @Schema(implementation = Integer.class)) final @PathVariable("quickbookCustomerId") Integer quickbookCustomerId,
            @Parameter(description = "Customer ID", schema = @Schema(implementation = Integer.class)) final @PathVariable("customerId") Integer customerId,
            final HttpServletRequest request
    ) {
        return quickbookCustomerService.editMappingCustomerToQuickbook(quickbookCustomerId, customerId, request);
    }

}
