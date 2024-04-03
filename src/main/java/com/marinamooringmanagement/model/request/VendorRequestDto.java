package com.marinamooringmanagement.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Represents a vendor request data transfer object (DTO) used for creating or updating vendor entities.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VendorRequestDto implements Serializable {

    private static final long serialVersionUID = 550268603576L;

    /**
     * Unique identifier for the vendor.
     */
    private Integer id;

    /**
     * Name of the vendor.
     */
    @NotNull(message = "Vendor Name can't be blank")
    private String vendorName;

    /**
     * Slot and date information for the vendor request.
     */
    private String slotAndDate;

    /**
     * Moorings information for the vendor request.
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
