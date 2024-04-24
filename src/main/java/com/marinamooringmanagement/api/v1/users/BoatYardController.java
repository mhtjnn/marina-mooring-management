package com.marinamooringmanagement.api.v1.users;

import com.marinamooringmanagement.model.dto.BoatYardDto;
import com.marinamooringmanagement.model.request.BoatYardRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.BoatYardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE;

/**
 * Controller class for managing Boat Yard operations.
 */
@RestController
@Validated
@RequestMapping(value = "/api/v1/boatyard")
public class BoatYardController {

    @Autowired
    private BoatYardService boatYardService;

    /**
     * Endpoint for creating a new Boat Yard.
     *
     * @param boatYardRequestDto The BoatYardDto containing the details of the Boat Yard to be created.
     * @return A BasicRestResponse indicating the status of the operation.
     */

    @Operation(
            tags = "Save BoatYard in the database",
            description = "API to save BoatYard in the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "400"
                    )
            }

    )
    @PostMapping(value = "/",
            produces = {"application/json"})
    public BasicRestResponse saveBoatYard(@Valid @RequestBody BoatYardRequestDto boatYardRequestDto
    ) {

        return boatYardService.saveBoatYard(boatYardRequestDto);
    }

    /**
     * Endpoint for retrieving a list of Boat Yards.
     *
     * @param pageNumber The page number of the results (default: 0).
     * @param pageSize   The size of each page (default: 10).
     * @param sortBy     The field to sort by (default: boatyardId).
     * @param sortDir    The direction of sorting (default: asc).
     * @return A list of BoatYardDto objects.
     */

    @Operation(
            tags = "Fetch BoatYard from the database",
            description = "API to fetch BoatYard from the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "400"
                    )
            }

    )
    @GetMapping(value = "/",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public List<BoatYardDto> getBoatYards(
            @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "boatyardId", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        return boatYardService.getBoatYard(pageNumber, pageSize, sortBy, sortDir);
    }

    /**
     * Endpoint for retrieving a Boat Yard by its ID.
     *
     * @param id The ID of the Boat Yard to retrieve.
     * @return The BoatYardDto object corresponding to the given ID.
     */
    @GetMapping(value = "/{id}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public BoatYardDto getBoatYard(@PathVariable(value = "id") Integer id) {
        return this.boatYardService.getbyId(id);
    }

    /**
     * Endpoint for deleting a Boat Yard by its ID.
     *
     * @param id The ID of the Boat Yard to delete.
     * @return A BasicRestResponse indicating the status of the operation.
     */

    @Operation(
            tags = "Delete BoatYard from the database",
            description = "API to delete BoatYard from the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "400"
                    )
            }

    )
    @DeleteMapping(value = "/{id}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse deleteBoatYard(@PathVariable(value = "id") Integer id) {

        return boatYardService.deleteBoatYardbyId(id);
    }

    /**
     * Endpoint for updating a Boat Yard.
     *
     * @param id                 The ID of the Boat Yard to update.
     * @param boatYardRequestDto The BoatYardDto containing the updated details.
     * @return A BasicRestResponse indicating the status of the operation.
     */

    @Operation(
            tags = "Update BoatYard in the database",
            description = "API to update BoatYard in the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "400"
                    )
            }

    )
    @PutMapping(value = "/{id}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse updateBoatYard(
            @PathVariable(value = "id", required = true) Integer id,
            @Valid @RequestBody BoatYardRequestDto boatYardRequestDto
    ) {
        return boatYardService.updateBoatYard(boatYardRequestDto, id);
    }

}
