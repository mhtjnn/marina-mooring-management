package com.marinamooringmanagement.api.v1.users;

import com.marinamooringmanagement.model.request.VendorRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.VendorResponseDto;
import com.marinamooringmanagement.service.VendorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE;

/**
 * REST Controller for handling vendor-related operations.
 */
@RestController
@RequestMapping("/api/v1/vendor")
@Validated
public class VendorController {

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
    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public BasicRestResponse fetchVendors(
            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer page,
            @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer size,
            @RequestParam(value = "sortBy", defaultValue = "vendorName", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        List<VendorResponseDto> vendorResponseDtoList = vendorService.fetchVendors(page, size, sortBy, sortDir);

        response.setMessage("List of vendors in the database");
        response.setContent(vendorResponseDtoList);
        response.setStatus(HttpStatus.OK.value());
        response.setTime(new Timestamp(System.currentTimeMillis()));

        return response;
    }

    /**
     * Saves a new vendor.
     *
     * @param requestDto Request DTO containing vendor details
     * @return BasicRestResponse indicating the status of the operation
     */
    @RequestMapping(value = "/",
            method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public BasicRestResponse saveVendor(
            @Valid @RequestBody VendorRequestDto requestDto
    ) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        vendorService.saveVendor(requestDto);

        response.setMessage("Vendor Saved Successfully");
        response.setTime(new Timestamp(System.currentTimeMillis()));
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    /**
     * Deletes a vendor by ID.
     *
     * @param vendorId ID of the vendor to be deleted
     * @return BasicRestResponse indicating the status of the deletion operation
     */
    @RequestMapping(value = "/{id}",
            method = RequestMethod.DELETE,
            produces = {"application/json", "application/xml"})
    public BasicRestResponse deleteVendor(
            @PathVariable("id") Integer vendorId
    ) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        final String message = vendorService.deleteVendor(vendorId);

        response.setMessage(message);
        response.setTime(new Timestamp(System.currentTimeMillis()));
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    /**
     * Updates an existing vendor.
     *
     * @param requestDto Request DTO containing updated vendor details
     * @param vendorId   ID of the vendor to be updated
     * @return BasicRestResponse indicating the status of the operation
     */
    @RequestMapping(value = "/{id}",
            method = RequestMethod.PUT,
            produces = {"application/json", "application/xml"})
    public BasicRestResponse updateVendor(
            @Valid @RequestBody VendorRequestDto requestDto,
            @PathVariable("id") Integer vendorId
    ) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        vendorService.updateVendor(requestDto, vendorId);

        response.setStatus(HttpStatus.OK.value());
        response.setTime(new Timestamp(System.currentTimeMillis()));
        response.setMessage("Vendor updated successfully");

        return response;
    }
}
