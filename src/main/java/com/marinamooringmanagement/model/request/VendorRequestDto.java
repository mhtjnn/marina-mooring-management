package com.marinamooringmanagement.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @NotNull(message = "Company name cannot be blank")
    private String companyName;

    /**
     * Phone number of the company associated with the vendor.
     */
    @NotNull(message = "Phone number cannot be blank")
    @Pattern(regexp = "^.{10}$|^.{12}$", message = "Invalid phone number format.")
    private String companyPhoneNumber;

    /**
     * Website URL of the company associated with the vendor.
     */
    @Pattern(regexp = "^(https?://)?(www\\.)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}([/a-zA-Z0-9-._~:?#[\\\\]@!$&'()*+,;=%]*)?$", message = "Invalid website format.")
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
    @NotNull(message = "State cannot be blank")
    private Integer stateId;

    /**
     * Country where the vendor is located.
     */
    @NotNull(message = "Country cannot be blank")
    private Integer countryId;

    /**
     * ZIP code of the vendor's location.
     */
    @Pattern(regexp = "^\\d{3,10}(-\\d{3,10})?$|^[A-Za-z]\\d[A-Za-z][ -]?\\d[A-Za-z]\\d$|^[A-Z]{1,2}\\d[A-Z\\d]? \\d[A-Z]{2}$", message = "Invalid Zipcode format.")
    private String zipCode;

    /**
     * Email address of the company associated with the vendor.
     */
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email address format.")
    private String companyEmail;

    /**
     * Account number associated with the vendor.
     */
    @Pattern(regexp = "^\\d{8,12}$", message = "Invalid account number format (must be 8 or 12 digits)")
    private String accountNumber;

    private String remitStreet;

    private String remitApt;

    private Integer remitStateId;

    private Integer remitCountryId;

    @Pattern(regexp = "^\\d{3,10}(-\\d{3,10})?$|^[A-Za-z]\\d[A-Za-z][ -]?\\d[A-Za-z]\\d$|^[A-Z]{1,2}\\d[A-Z\\d]? \\d[A-Z]{2}$", message = "Invalid Zipcode format.")
    private String remitZipCode;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email address format.")
    private String remitEmailAddress;

    /**
     * First name of the primary contact person associated with the vendor.
     */
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Invalid name format.")
    private String firstName;

    /**
     * Last name of the primary contact person associated with the vendor.
     */
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Invalid name format.")
    private String lastName;

    /**
     * Phone number of the sales representative associated with the vendor.
     */
    @NotNull(message = "Phone number cannot be blank")
    @Pattern(regexp = "^.{10}$|^.{12}$", message = "Invalid phone number format.")
    private String salesRepPhoneNumber;

    /**
     * Email address of the sales representative associated with the vendor.
     */
    @NotNull(message = "Sales Representation email cannot be blank")
    private String salesRepEmail;

    /**
     * Note or additional information about the sales representative associated with the vendor.
     */
    private String salesRepNote;
}