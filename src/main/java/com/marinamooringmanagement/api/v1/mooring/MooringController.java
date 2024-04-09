package com.marinamooringmanagement.api.v1.mooring;

import com.marinamooringmanagement.model.request.MooringRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.MooringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE;

/**
 * REST Controller for handling mooring-related operations.
 */
@RestController
@RequestMapping("/api/v1/mooring")
@Validated
public class MooringController {

    @Autowired
    private MooringService mooringService;

    /**
     * Fetches a list of moorings based on pagination and sorting parameters.
     *
     * @param page    Page number for pagination (default: 1)
     * @param size    Page size for pagination (default: 10)
     * @param sortBy  Field to sort by (default: "id")
     * @param sortDir Sorting direction (asc/desc, default: "asc")
     * @return BasicRestResponse containing the fetched moorings
     */
    @Operation(
            tags = "Fetch moorings from the database",
            description = "API to fetch moorings from the database",
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
    public BasicRestResponse fetchMoorings(
            @Parameter(description = "Page Number", schema = @Schema(implementation = Integer.class)) @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer page,
            @Parameter(description = "Page Size", schema = @Schema(implementation = Integer.class)) @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer size,
            @Parameter(description = "Sort By(field)", schema = @Schema(implementation = String.class)) @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @Parameter(description = "Sort Dir(asc --> ascending or des --> descending)", schema = @Schema(implementation = String.class)) @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        return mooringService.fetchMoorings(page, size, sortBy, sortDir);
    }

    /**
     * Saves a new mooring.
     *
     * @param dto Request DTO containing mooring details
     * @return BasicRestResponse indicating the status of the operation
     */
    @Operation(
            tags = "Save Mooring in the database",
            description = "API to save mooring in the database",
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
    public BasicRestResponse saveMooring(
            @Parameter(description = "Properties of a mooring", schema = @Schema(implementation = MooringRequestDto.class)) @Valid @RequestBody MooringRequestDto dto
    ) {
        return mooringService.saveMooring(dto);
    }

    /**
     * Updates an existing mooring.
     *
     * @param dto       Request DTO containing updated mooring details
     * @param mooringId ID of the mooring to be updated
     * @return BasicRestResponse indicating the status of the operation
     */
    @Operation(
            tags = "Update moorings in the database",
            description = "API to delete moorings in the database",
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
            @Parameter(description = "Fields to update in the mooring", schema = @Schema(implementation = MooringRequestDto.class)) @Valid @RequestBody MooringRequestDto dto,
            @Parameter(description = "ID of the mooring to be updated", schema = @Schema(implementation = Integer.class)) @PathVariable("id") Integer mooringId
    ) {
        return mooringService.updateMooring(dto, mooringId);
    }

    /**
     * Deletes a mooring by ID.
     *
     * @param id ID of the mooring to be deleted
     * @return BasicRestResponse indicating the status of the deletion operation
     */
    @Operation(
            tags = "Delete mooring from the database",
            description = "API to delete mooring from the database",
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
            @Parameter(description = "Id of the mooring to be deleted", schema = @Schema(implementation = Integer.class)) @PathVariable("id") Integer id
    ) {

        return mooringService.deleteMooring(id);

    }
}
