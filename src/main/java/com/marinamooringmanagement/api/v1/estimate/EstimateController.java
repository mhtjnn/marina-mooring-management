package com.marinamooringmanagement.api.v1.estimate;

import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.EstimateRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.EstimateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping("/api/v1/estimate")
@Validated
@CrossOrigin
public class EstimateController {

    @Autowired
    private EstimateService estimateService;

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
            tags = "Fetch estimates from the database",
            description = "API to estimates from the database",
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
    public BasicRestResponse fetchEstimates(
            @Parameter(description = "Page Number", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            @Parameter(description = "Page Size", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
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
        return estimateService.fetchEstimates(baseSearchRequest, searchText, request);
    }

    /**
     * Saves a new work order.
     *
     * @param estimateRequestDto Request DTO containing work order details
     * @return BasicRestResponse indicating the status of the operation
     */
    @Operation(
            tags = "Save estimate in the database",
            description = "API to save estimates in the database",
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
    public BasicRestResponse saveEstimate(
            @Parameter(description = "Properties of a work order", schema = @Schema(implementation = EstimateRequestDto.class)) final @Valid @RequestBody EstimateRequestDto  estimateRequestDto,
            final HttpServletRequest request
    ) {
        return estimateService.saveEstimate(estimateRequestDto, request);
    }

    /**
     * Updates an existing work order.
     *
     * @param estimateRequestDto Request DTO containing updated work order details
     * @param estimateId ID of the work order to be updated
     * @return BasicRestResponse indicating the status of the operation
     */
    @Operation(
            tags = "Update estimates in the database",
            description = "API to update estimates in the database",
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
            @Parameter(description = "Fields to update in the work order", schema = @Schema(implementation = EstimateRequestDto.class)) final @Valid @RequestBody EstimateRequestDto estimateRequestDto,
            @Parameter(description = "ID of the work order to be updated", schema = @Schema(implementation = Integer.class)) final @PathVariable("id") Integer estimateId,
            final HttpServletRequest request
    ) {
        return estimateService.updateEstimate(estimateRequestDto, estimateId, request);
    }

    /**
     * Deletes a work order by ID.
     *
     * @param id ID of the work order to be deleted
     * @return BasicRestResponse indicating the status of the deletion operation
     */
    @Operation(
            tags = "Delete estimates from the database",
            description = "API to delete estimates from the database",
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
        return estimateService.deleteEstimate(id, request);
    }


}
