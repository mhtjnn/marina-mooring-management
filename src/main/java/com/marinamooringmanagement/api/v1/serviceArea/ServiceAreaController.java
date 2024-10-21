package com.marinamooringmanagement.api.v1.serviceArea;

import com.marinamooringmanagement.constants.Authority;
import com.marinamooringmanagement.exception.handler.GlobalExceptionHandler;
import com.marinamooringmanagement.model.dto.ServiceAreaDto;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.ServiceAreaRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.ServiceAreaService;
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

@RestController
@RequestMapping(value = "/api/v1/serviceArea")
@Validated
@CrossOrigin
@Tag(name = "Service area Controller", description = "These are API's for service area.")
public class ServiceAreaController extends GlobalExceptionHandler {

    @Autowired
    private ServiceAreaService serviceAreaService;

    /**
     * Endpoint for creating a new Boatyard.
     *
     * @param serviceAreaRequestDto The serviceAreaRequest containing the details of the service area to be created.
     * @return A BasicRestResponse indicating the status of the operation.
     */
    @PostMapping(value = "/", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "API to save service area in the database",
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
    public BasicRestResponse saveServiceArea(
            @Valid @RequestBody ServiceAreaRequestDto serviceAreaRequestDto,
            final HttpServletRequest request
    ) {
        return serviceAreaService.saveServiceArea(serviceAreaRequestDto, request);
    }

    /**
     * Endpoint for retrieving a list of ServiceAreas.
     *
     * @param pageNumber The page number of the results (default: 0).
     * @param pageSize   The size of each page (default: 10).
     * @param sortBy     The field to sort by (default: boatyardId).
     * @param sortDir    The direction of sorting (default: asc).
     * @return A list of {@link   ServiceAreaRequestDto} objects.
     */
    @GetMapping(value = "/", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "API to fetch service area from the database",
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
    public BasicRestResponse fetchServiceAreas(
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
        return serviceAreaService.fetchServiceAreas(baseSearchRequest, searchText, request);
    }

    /**
     * Fetches Moorings related to a specific boatyard from the database.
     *
     * @param id the ID of the boatyard.
     * @return a {@link BasicRestResponse} containing the moorings related to the boatyard.
     */
    @Operation(
            summary = "API to fetch Moorings related to service area from the database",
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
    @GetMapping(value = "/fetchMooringsWithServiceArea/{id}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse fetchMooringsWithServiceArea(
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
        return serviceAreaService.fetchMooringsWithServiceArea(baseSearchRequest, id, request);
    }

    /**
     * Endpoint for retrieving a Boatyard by its ID.
     *
     * @param id The ID of the Boatyard to retrieve.
     * @return The {@code  ServiceAreaRequestDto} object corresponding to the given ID.
     */
    @GetMapping(value = "/{id}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public ServiceAreaDto getServiceArea(@PathVariable(value = "id") Integer id) {
        return serviceAreaService.getById(id);
    }

    /**
     * Endpoint for deleting a Boatyard by its ID.
     *
     * @param id The ID of the Boatyard to delete.
     * @return A BasicRestResponse indicating the status of the operation.
     */
    @Operation(
            summary = "API to delete service area from the database",
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
    public BasicRestResponse deleteServiceArea(
            @PathVariable(value = "id") Integer id,
            final HttpServletRequest request
    ) {
        return serviceAreaService.deleteServiceAreaById(id, request);
    }

    /**
     * Endpoint for updating a Boatyard.
     *
     * @param id                 The ID of the Boatyard to update.
     * @param serviceAreaRequestDto The {@code  ServiceAreaRequestDto} containing the updated details.
     * @return A BasicRestResponse indicating the status of the operation.
     */
    @PutMapping(value = "/{id}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "API to update service area in the database",
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
    public BasicRestResponse updateServiceArea(
            @PathVariable(value = "id") Integer id,
            @Valid @RequestBody ServiceAreaRequestDto serviceAreaRequestDto,
            final HttpServletRequest request
    ) {
        return serviceAreaService.updateServiceArea(serviceAreaRequestDto, id, request);
    }

}
