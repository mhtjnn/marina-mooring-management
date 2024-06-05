package com.marinamooringmanagement.model.request;

import com.marinamooringmanagement.model.dto.CountryDto;
import com.marinamooringmanagement.model.dto.StateDto;
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
    private Integer stateId;

    /**
     * Country where the vendor is located.
     */
    private Integer countryId;

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

    private String remitStreet;

    private String remitApt;

    private Integer remitStateId;

    private Integer remitCountryId;

    private String remitZipCode;

    private String remitEmailAddress;

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
}