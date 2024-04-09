package com.marinamooringmanagement.model.dto;

import jakarta.persistence.Column;
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
     * Name of the company associated with the vendor.
     */
    private String companyName;

    /**
     * Phone number of the company associated with the vendor.
     */
    private String companyPhoneNumber;

    /**
     * Website URL of the company associated with the vendor.
     */
    private String website;

    /**
     * Street address of the vendor.
     */
    private String street;

    /**
     * Apartment or suite number of the vendor.
     */
    private String aptSuite;

    /**
     * State where the vendor is located.
     */
    private String state;

    /**
     * Country where the vendor is located.
     */
    private String country;

    /**
     * ZIP code of the vendor's location.
     */
    private Integer zipCode;

    /**
     * Email address of the company associated with the vendor.
     */
    private String companyEmail;

    /**
     * Account number associated with the vendor.
     */
    private String accountNumber;

    /**
     * First name of the primary contact person associated with the vendor.
     */
    private String firstName;

    /**
     * Last name of the primary contact person associated with the vendor.
     */
    private String lastName;

    /**
     * Phone number of the sales representative associated with the vendor.
     */
    private String salesRepPhoneNumber;

    /**
     * Email address of the sales representative associated with the vendor.
     */
    private String salesRepEmail;

    /**
     * Note or additional information about the sales representative associated with the vendor.
     */
    private String salesRepNote;

    /**
     * Flag indicating whether the sales representative associated with the vendor is primary or not.
     */
    private boolean primarySalesRep;
}
