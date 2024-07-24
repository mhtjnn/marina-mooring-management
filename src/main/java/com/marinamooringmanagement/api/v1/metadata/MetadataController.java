package com.marinamooringmanagement.api.v1.metadata;

import com.marinamooringmanagement.exception.handler.GlobalExceptionHandler;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.CountryService;
import com.marinamooringmanagement.service.MetadataService;
import com.marinamooringmanagement.service.RoleService;
import com.marinamooringmanagement.service.StateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE;

/**
 * Controller class for handling metadata-related API endpoints.
 */
@RestController
@RequestMapping("/api/v1/metadata")
@Validated
@CrossOrigin
@Tag(name = "Metadata Controller", description = "These are API's for metadata")
public class MetadataController extends GlobalExceptionHandler {

    @Autowired
    private StateService stateService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MetadataService metadataService;

    /**
     * Fetches countries from the database.
     *
     * @param page    Page number
     * @param size    Page size
     * @param sortBy  Field to sort by
     * @param sortDir Sort direction (asc or desc)
     * @return BasicRestResponse containing the list of countries
     */
    @Operation(
            summary = "API to fetch countries from the database",
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
            value = "/countries",
            method = RequestMethod.GET,
            produces = {"application/json"}
    )
    public BasicRestResponse fetchCountries(
            @Parameter(description = "Page Number", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer page,
            @Parameter(description = "Page Size", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "size", defaultValue = "500", required = false) Integer size,
            @Parameter(description = "Sort By(field)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @Parameter(description = "Sort Dir(asc --> ascending or des --> descending)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(page)
                .pageSize(size)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
        return countryService.fetchCountries(baseSearchRequest);
    }

    /**
     * Fetches states from the database.
     *
     * @param page    Page number
     * @param size    Page size
     * @param sortBy  Field to sort by
     * @param sortDir Sort direction (asc or desc)
     * @return BasicRestResponse containing the list of states
     */
    @Operation(
            summary = "API to fetch states from the database",
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
            value = "/states",
            method = RequestMethod.GET,
            produces = {"application/json"}
    )
    public BasicRestResponse fetchStates(
            @Parameter(description = "Page Number", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer page,
            @Parameter(description = "Page Size", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "size", defaultValue = "500", required = false) Integer size,
            @Parameter(description = "Sort By(field)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @Parameter(description = "Sort Dir(asc --> ascending or des --> descending)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(page)
                .pageSize(size)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
        return stateService.fetchStates(baseSearchRequest);
    }

    /**
     * Fetches roles from the database.
     *
     * @param page    Page number
     * @param size    Page size
     * @param sortBy  Field to sort by
     * @param sortDir Sort direction (asc or desc)
     * @return BasicRestResponse containing the list of roles
     */
    @Operation(
            summary = "API to fetch roles from the database",
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
            value = "/roles",
            method = RequestMethod.GET,
            produces = {"application/json"}
    )
    public BasicRestResponse fetchRoles(
            @Parameter(description = "Page Number", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer page,
            @Parameter(description = "Page Size", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer size,
            @Parameter(description = "Sort By(field)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @Parameter(description = "Sort Dir(asc --> ascending or des --> descending)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(page)
                .pageSize(size)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
        return roleService.fetchRoles(baseSearchRequest);
    }

    @Operation(
            summary = "API to fetch status of MOORING from the database",
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
    @GetMapping("/status")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchStatus(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchStatus(baseSearchRequest);
    }

    @Operation(
            summary = "API to fetch types of boat from the database",
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
    @GetMapping("/boatType")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchBoatType(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchBoatType(baseSearchRequest);
    }

    @Operation(
            summary = "API to fetch size of weights from the database",
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
    @GetMapping("/sizeOfWeight")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchSizeOfWeight(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchSizeOfWeight(baseSearchRequest);
    }

    @Operation(
            summary = "API to fetch type of weights from the database",
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
    @GetMapping("/typeOfWeight")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchTypeOfWeight(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchTypeOfWeight(baseSearchRequest);
    }

    @Operation(
            summary = "API to fetch top chain conditions from the database",
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
    @GetMapping("/topChainCondition")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchTopChainConditions(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchTopChainCondition(baseSearchRequest);
    }

    @Operation(
            summary = "API to fetch eye conditions from the database",
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
    @GetMapping("/eyeCondition")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchEyeConditions(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchEyeConditions(baseSearchRequest);
    }

    @Operation(
            summary = "API to fetch bottom chain conditions from the database",
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
    @GetMapping("/bottomChainConditions")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchBottomChainConditions(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchBottomChainConditions(baseSearchRequest);
    }

    @Operation(
            summary = "API to fetch shackle swivel conditions from the database",
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
    @GetMapping("/shackleSwivelConditions")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchShackleSwivelConditions(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchShackleSwivelConditions(baseSearchRequest);
    }

    @Operation(
            summary = "API to fetch customers from the database",
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
    @GetMapping("/customers")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchCustomers(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchCustomers(baseSearchRequest, request);
    }

    @Operation(
            summary = "API to fetch boatyards from the database",
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
    @GetMapping("/boatyards")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchBoatyards(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchBoatyards(baseSearchRequest, request);
    }

    @Operation(
            summary = "API to fetch mooring ids from the database",
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
    @GetMapping("/mooringIds")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchMooringIds(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchMooringIds(baseSearchRequest, request);
    }

    @Operation(
            summary = "API to fetch users with technician role from the database",
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
    @GetMapping("/technicians")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchTechnicians(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchTechnicians(baseSearchRequest, request);
    }

    @Operation(
            summary = "API to fetch status of WORK ORDER from the database",
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
    @RequestMapping(value = {"/workOrderStatus"}, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchSaveAndEditWorkOrderStatus(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchWorkOrderStatus(baseSearchRequest, request);
    }

    @Operation(
            summary = "API to fetch types of inventory from the database",
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
    @GetMapping("/inventoryType")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchInventoryTypes(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize,
            final HttpServletRequest request
            ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchInventoryType(baseSearchRequest, request);
    }

    @Operation(
            summary = "API to fetch customer owner from the database",
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
    @GetMapping("/customerOwners")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchCustomerOwners(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize,
            final @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            final @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();

        return metadataService.fetchCustomerOwners(baseSearchRequest, request);
    }

    @Operation(
            summary = "API to fetch mooring based on customer from the database",
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
    @GetMapping("/mooringsBasedOnCustomerId/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchMooringsBasedOnCustomerId(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize,
            @PathVariable(value = "customerId") final Integer customerId,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchMooringsBasedOnCustomerId(baseSearchRequest, customerId, request);
    }

    @Operation(
            summary = "API to fetch mooring based on boatyard from the database",
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
    @GetMapping("/mooringsBasedOnBoatyardId/{boatyardId}")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchMooringsBasedOnBoatyardId(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize,
            @PathVariable(value = "boatyardId") final Integer boatyardId,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchMooringsBasedOnBoatyardId(baseSearchRequest, boatyardId, request);
    }

    @Operation(
            summary = "API to fetch customer based on mooring from the database",
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
    @GetMapping("/customerBasedOnMooringId/{mooringId}")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchCustomerBasedOnMooringId(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize,
            @PathVariable(value = "mooringId") final Integer mooringId,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchCustomerBasedOnMooringId(baseSearchRequest, mooringId, request);
    }

    @Operation(
            summary = "API to fetch boatyard based on mooring from the database",
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
    @GetMapping("/boatyardBasedOnMooringId/{mooringId}")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchBoatyardBasedOnMooringId(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize,
            @PathVariable(value = "mooringId") final Integer mooringId,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchBoatyardBasedOnMooringId(baseSearchRequest, mooringId, request);
    }

    @Operation(
            summary = "API to fetch mooring based on customer nad boatyard from the database",
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
    @GetMapping("/mooringBasedOnCustomerIdAndBoatyardId/{customerId}/{boatyardId}")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchMooringBasedOnCustomerIdAndBoatyardId(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize,
            @PathVariable(value = "customerId") final Integer customerId,
            @PathVariable(value = "boatyardId") final Integer boatyardId,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchMooringBasedOnCustomerIdAndMooringId(baseSearchRequest, customerId, boatyardId, request);
    }

    @Operation(
            summary = "API to fetch types of customer from the database",
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
    @GetMapping("/customerTypes")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchCustomerTypes(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchCustomerTypes(baseSearchRequest);
    }

    @Operation(
            summary = "API to fetch types of service area from the database",
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
    @GetMapping("/serviceAreaTypes")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchServiceAreaTypes(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchServiceAreaTypes(baseSearchRequest);
    }

    @Operation(
            summary = "API to fetch service areas from the database",
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
    @GetMapping("/serviceAreas")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchServiceAreas(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchServiceAreas(baseSearchRequest, request);
    }

    @Operation(
            summary = "API to fetch quickbook customers from the database",
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
    @GetMapping("/quickbookCustomers")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchQuickbookCustomers(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchQuickbookCustomers(baseSearchRequest, request);
    }

    @Operation(
            summary = "API to fetch types of payment from the database",
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
    @GetMapping("/paymentTypes")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchPaymentTypes(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchPaymentTypes(baseSearchRequest, request);
    }
}

