package com.marinamooringmanagement.api.v1.metadata;

import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.CountrySearchRequest;
import com.marinamooringmanagement.model.request.StateSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.CountryService;
import com.marinamooringmanagement.service.RoleService;
import com.marinamooringmanagement.service.StateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
@CrossOrigin("*")
public class MetadataController {

    @Autowired
    private StateService stateService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private RoleService roleService;

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
        final CountrySearchRequest countrySearchRequest = CountrySearchRequest.builder().build();
        countrySearchRequest.setPageNumber(page);
        countrySearchRequest.setPageSize(size);
        countrySearchRequest.setSort(new BaseSearchRequest().getSort(sortBy, sortDir));
        return countryService.fetchCountries(countrySearchRequest);
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
        final StateSearchRequest stateSearchRequest = StateSearchRequest.builder().build();
        stateSearchRequest.setPageNumber(page);
        stateSearchRequest.setPageSize(size);
        stateSearchRequest.setSort(new BaseSearchRequest().getSort(sortBy, sortDir));
        return stateService.fetchStates(stateSearchRequest);
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
        return roleService.fetchRoles();
    }

}

