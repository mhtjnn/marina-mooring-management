package com.marinamooringmanagement.api.v1.boatyard;

import com.marinamooringmanagement.constants.Authority;
import com.marinamooringmanagement.exception.handler.GlobalExceptionHandler;
import com.marinamooringmanagement.model.dto.BoatyardDto;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.BoatyardRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.BoatyardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE;

/**
 * Controller class for managing Boatyard operations.
 */
@RestController
@RequestMapping(value = "/api/v1/boatyard")
@Validated
@CrossOrigin()
@Tag(name = "Boatyard Controller", description = "These are API's for boatyard.")
public class BoatyardController extends GlobalExceptionHandler {

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
            summary = "API to save BoatYard in the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse saveBoatyard(
            @Valid @RequestBody BoatyardRequestDto boatYardRequestDto,
            final HttpServletRequest request
    ) {
        return boatyardService.saveBoatyard(boatYardRequestDto, request);
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
            summary = "API to fetch BoatYard from the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse fetchBoatyards(
            final @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            final @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            final @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            final @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            final @RequestParam(value = "searchText", required = false) String searchText,
            final HttpServletRequest request
            ) {
        BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
        return boatyardService.fetchBoatyards(baseSearchRequest, searchText, request);
    }

    /**
     * Fetches Moorings related to a specific boatyard from the database.
     *
     * @param id the ID of the boatyard.
     * @return a {@link BasicRestResponse} containing the moorings related to the boatyard.
     */
    @Operation(
            summary = "API to fetch Moorings related to boatyard from the database",
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
    @GetMapping(value = "/fetchMooringsWithBoatyard/{id}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse fetchMooringsWithBoatyard(
            @PathVariable("id") final Integer id,
            final @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            final @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            final @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            final @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            final HttpServletRequest request
    ) {
        BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
        return boatyardService.fetchMooringsWithBoatyard(baseSearchRequest, id, request);
    }

    /**
     * Endpoint for retrieving a Boatyard by its ID.
     *
     * @param id The ID of the Boatyard to retrieve.
     * @return The BoatYardDto object corresponding to the given ID.
     */
    @GetMapping(value = "/{id}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
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
            summary = "API to delete BoatYard from the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse deleteBoatyard(
            @PathVariable(value = "id") Integer id,
            final HttpServletRequest request
    ) {
        return boatyardService.deleteBoatyardById(id, request);
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
            summary = "API to update BoatYard in the database",
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
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse updateBoatyard(
            @PathVariable(value = "id") Integer id,
            @Valid @RequestBody BoatyardRequestDto boatYardRequestDto,
            final HttpServletRequest request
    ) {
        return boatyardService.updateBoatyard(boatYardRequestDto, id, request);
    }

}

