package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.VendorRequestDto;
import com.marinamooringmanagement.model.response.VendorResponseDto;

import java.util.List;


/**
 * Service interface for managing Vendor entities.
 */
public interface VendorService {

    /**
     * Fetches a paginated list of vendors.
     *
     * @param page    the page number
     * @param size    the page size
     * @param sortBy  the field to sort by
     * @param sortDir the sort direction (asc or desc)
     * @return a list of vendor response DTOs
     */
    List<VendorResponseDto> fetchVendors(Integer page, Integer size, String sortBy, String sortDir);

    /**
     * Saves a vendor based on the provided request DTO.
     *
     * @param requestDto the vendor request DTO
     */
    void saveVendor(VendorRequestDto requestDto);

    /**
     * Deletes a vendor based on the provided vendor ID.
     *
     * @param vendorId the vendor ID
     * @return a message indicating the deletion status
     */
    String deleteVendor(Integer vendorId);

    /**
     * Updates a vendor based on the provided request DTO and vendor ID.
     *
     * @param requestDto the vendor request DTO
     * @param vendorId   the vendor ID
     */
    void updateVendor(VendorRequestDto requestDto, Integer vendorId);
}

