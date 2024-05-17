package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.VendorRequestDto;
import com.marinamooringmanagement.model.request.VendorSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;

import java.util.List;


/**
 * Service interface for managing Vendor entities.
 */
public interface VendorService {


    BasicRestResponse fetchVendors(final VendorSearchRequest vendorSearchRequest);

    /**
     * Saves a vendor based on the provided request DTO.
     *
     * @param requestDto the vendor request DTO
     */
    BasicRestResponse saveVendor(final VendorRequestDto requestDto);

    /**
     * Deletes a vendor based on the provided vendor ID.
     *
     * @param vendorId the vendor ID
     * @return a message indicating the deletion status
     */
    BasicRestResponse deleteVendor(final Integer vendorId);

    /**
     * Updates a vendor based on the provided request DTO and vendor ID.
     *
     * @param requestDto the vendor request DTO
     * @param vendorId   the vendor ID
     */
    BasicRestResponse updateVendor(final VendorRequestDto requestDto, final Integer vendorId);
}
