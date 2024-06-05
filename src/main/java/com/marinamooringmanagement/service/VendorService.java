package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.VendorRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.parser.HttpParser;

import java.util.List;


/**
 * Service interface for managing Vendor entities.
 */
public interface VendorService {

    /**
     * Fetches a list of vendors based on the provided search request parameters and search text.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @param searchText the text used to search for specific vendors by name, location, or other relevant criteria.
     * @return a BasicRestResponse containing the results of the vendor search.
     */
    BasicRestResponse fetchVendors(final BaseSearchRequest baseSearchRequest, final String searchText, final HttpServletRequest request);

    /**
     * Saves a vendor based on the provided request DTO.
     *
     * @param requestDto the vendor request DTO
     */
    BasicRestResponse saveVendor(final VendorRequestDto requestDto, final HttpServletRequest request);

    /**
     * Deletes a vendor based on the provided vendor ID.
     *
     * @param vendorId the vendor ID
     * @return a message indicating the deletion status
     */
    BasicRestResponse deleteVendor(final Integer vendorId, final HttpServletRequest request);

    /**
     * Updates a vendor based on the provided request DTO and vendor ID.
     *
     * @param requestDto the vendor request DTO
     * @param vendorId   the vendor ID
     */
    BasicRestResponse updateVendor(final VendorRequestDto requestDto, final Integer vendorId, final HttpServletRequest request);
}
