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
            tags = "Fetch countries from the database",
            description = "API to fetch countries from the database",
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
            tags = "Fetch states from the database",
            description = "API to fetch states from the database",
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
            tags = "Fetch roles from the database",
            description = "API to fetch roles from the database",
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

    @GetMapping("/pennantConditions")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchPennantConditions(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchPennantConditions(baseSearchRequest);
    }

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

    @GetMapping("/mooringsBasedOnCustomerId")
    @ResponseStatus(HttpStatus.OK)
    public BasicRestResponse fetchMooringsBasedOnCustomerId(
            @RequestParam(value = "pageNumber",defaultValue = DEFAULT_PAGE_NUM, required = false) final Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) final Integer pageSize,
            @RequestParam(value = "customerId") final Integer customerId,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();

        return metadataService.fetchMooringsBasedOnCustomerId(baseSearchRequest, customerId, request);
    }
}

