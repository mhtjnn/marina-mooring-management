package com.marinamooringmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Represents a vendor data transfer object (DTO) used for transferring vendor-related information.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VendorDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 55026860357976L;

    /**
     * Unique identifier for the vendor.
     */
    private Integer id;

    /**
     * Name of the vendor.
     */
    private String vendorName;

    /**
     * Slot and date information for the vendor.
     */
    private String slotAndDate;

    /**
     * Moorings information for the vendor.
     */
    private String moorings;

    /**
     * Number of users associated with the vendor.
     */
    private Integer noOfUsers;

    /**
     * Inventory price information for the vendor.
     */
    private String inventoryPrice;

    /**
     * Contact information for the vendor.
     */
    private String contact;
}

