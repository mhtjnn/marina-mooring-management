package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.response.metadata.CountryResponseDto;
import com.marinamooringmanagement.model.response.metadata.StateResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * The {@code VendorResponseDto} class represents a Data Transfer Object (DTO) for vendor response.
 * This class is used to transfer vendor data, including information about the company, contact details,
 * and sales representative information.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VendorResponseDto implements Serializable {

    private static final long serialVersionUID = 55268635079L;

    private Integer id;

    private String vendorName;

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
    private StateResponseDto stateResponseDto;

    /**
     * Country where the vendor is located.
     */
    private CountryResponseDto countryResponseDto;

    /**
     * ZIP code of the vendor's location.
     */
    private String zipCode;

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

    private StateResponseDto remitStateResponseDto;

    private CountryResponseDto remitCountryResponseDto;

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

    private Integer userId;

    private Integer inventoryItems;
}
