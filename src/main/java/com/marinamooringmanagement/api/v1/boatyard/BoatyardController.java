package com.marinamooringmanagement.api.v1.boatyard;

import com.marinamooringmanagement.model.dto.BoatyardDto;
import com.marinamooringmanagement.model.request.BoatyardRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.BoatyardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE;

/**
 * Controller class for managing Boatyard operations.
 */
@RestController
@Validated
@RequestMapping(value = "/api/v1/boatyard")
@CrossOrigin
public class BoatyardController {

    @Autowired
    private BoatyardService boatyardService;

    /**
     * Endpoint for creating a new Boatyard.
     *
     * @param boatYardRequestDto The BoatYardDto containing the details of the Boat Yard to be created.
     * @return A BasicRestResponse indicating the status of the operation.
     */
    @PostMapping(value = "/", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
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
    public BasicRestResponse saveBoatyard(@Valid @RequestBody BoatyardRequestDto boatYardRequestDto) {
        return boatyardService.saveBoatyard(boatYardRequestDto);
    }

    /**
     * Endpoint for retrieving a list of Boatyards.
     *
     * @param pageNumber The page number of the results (default: 0).
     * @param pageSize   The size of each page (default: 10).
     * @param sortBy     The field to sort by (default: boatyardId).
     * @param sortDir    The direction of sorting (default: asc).
     * @return A list of BoatYardDto objects.
     */
    @GetMapping(value = "/", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
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
    public BasicRestResponse fetchBoatyards(
            @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "boatyardId", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {
        return boatyardService.fetchBoatyards(pageNumber, pageSize, sortBy, sortDir);
    }

    /**
     * Endpoint for retrieving a Boatyard by its ID.
     *
     * @param id The ID of the Boatyard to retrieve.
     * @return The BoatYardDto object corresponding to the given ID.
     */
    @GetMapping(value = "/{id}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public BoatyardDto getBoatyard(@PathVariable(value = "id") Integer id) {
        return this.boatyardService.getbyId(id);
    }

    /**
     * Endpoint for deleting a Boatyard by its ID.
     *
     * @param id The ID of the Boatyard to delete.
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
    @DeleteMapping(value = "/{id}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public BasicRestResponse deleteBoatyard(@PathVariable(value = "id") Integer id) {
        return boatyardService.deleteBoatyardById(id);
    }

    /**
     * Endpoint for updating a Boatyard.
     *
     * @param id                 The ID of the Boatyard to update.
     * @param boatYardRequestDto The BoatYardDto containing the updated details.
     * @return A BasicRestResponse indicating the status of the operation.
     */
    @PutMapping(value = "/{id}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
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
    public BasicRestResponse updateBoatyard(
            @PathVariable(value = "id", required = true) Integer id,
            @Valid @RequestBody BoatyardRequestDto boatYardRequestDto) {
        return boatyardService.updateBoatyard(boatYardRequestDto, id);
    }

}

