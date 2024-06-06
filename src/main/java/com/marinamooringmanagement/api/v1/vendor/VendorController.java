package com.marinamooringmanagement.api.v1.vendor;

import com.marinamooringmanagement.exception.handler.GlobalExceptionHandler;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.VendorRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.VendorService;
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

/**
 * REST Controller for handling vendor-related operations.
 */
@RestController
@RequestMapping("/api/v1/vendor")
@Validated
@CrossOrigin("*")
public class VendorController extends GlobalExceptionHandler {

    @Autowired
    private VendorService vendorService;

    /**
     * Retrieves a list of vendors based on pagination and sorting parameters.
     *
     * @param page    Page number for pagination (default: 1)
     * @param size    Page size for pagination (default: 10)
     * @param sortBy  Field to sort by (default: "vendorName")
     * @param sortDir Sorting direction (asc/desc, default: "asc")
     * @return BasicRestResponse containing the fetched vendors
     */
    @Operation(
            tags = "Fetch vendors from the database",
            description = "API to fetch vendors from the database",
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
            method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public BasicRestResponse fetchVendors(
            @Parameter(description = "Page Number", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer page,
            @Parameter(description = "Page Size", schema = @Schema(implementation = Integer.class)) final @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer size,
            @Parameter(description = "Sort By(field to be compared while sorting)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @Parameter(description = "Sort Direction(asc --> ascending and dsc --> descending)", schema = @Schema(implementation = String.class)) final @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            @Parameter(description = "Search Text", schema = @Schema(implementation = String.class)) @RequestParam(value = "searchText", required = false) final String searchText,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(page)
                .pageSize(size)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
        return vendorService.fetchVendors(baseSearchRequest, searchText, request);
    }

    /**
     * Saves a new vendor.
     *
     * @param requestDto Request DTO containing vendor details
     * @return BasicRestResponse indicating the status of the operation
     */
    @Operation(
            tags = "Save vendor in the database",
            description = "API to save vendor in the database",
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
            produces = {"application/json", "application/xml"})
    public BasicRestResponse saveVendor(
            @Parameter(description = "Vendor to save in the database", schema = @Schema(implementation = VendorRequestDto.class)) final @Valid @RequestBody VendorRequestDto requestDto,
            final HttpServletRequest request
            ) {
        return vendorService.saveVendor(requestDto, request);

    }

    /**
     * Deletes a vendor by ID.
     *
     * @param vendorId ID of the vendor to be deleted
     * @return BasicRestResponse indicating the status of the deletion operation
     */
    @Operation(
            tags = "Delete vendor from the database",
            description = "API to delete vendor from the database",
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
            produces = {"application/json", "application/xml"})
    public BasicRestResponse deleteVendor(
            @Parameter(description = "Vendor ID to be deleted", schema = @Schema(implementation = Integer.class)) final @PathVariable("id") Integer vendorId,
            final HttpServletRequest request
    ) {
        return vendorService.deleteVendor(vendorId, request);
    }

    /**
     * Updates an existing vendor.
     *
     * @param requestDto Request DTO containing updated vendor details
     * @param vendorId   ID of the vendor to be updated
     * @return BasicRestResponse indicating the status of the operation
     */
    @Operation(
            tags = "Update vendor in the database",
            description = "API to update vendor in the database",
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
            produces = {"application/json", "application/xml"})
    public BasicRestResponse updateVendor(
            @Parameter(description = "Fields to update", schema = @Schema(implementation = VendorRequestDto.class)) final @Valid @RequestBody VendorRequestDto requestDto,
            @Parameter(description = "Vendor ID", schema = @Schema(implementation = Integer.class)) final @PathVariable("id") Integer vendorId,
            final HttpServletRequest request
    ) {
        return vendorService.updateVendor(requestDto, vendorId, request);
    }
}
