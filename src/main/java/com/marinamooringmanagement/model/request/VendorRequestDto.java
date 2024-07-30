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

    @NotNull(message = "Vendor name cannot be blank")
    private String vendorName;

    /**
     * Phone number of the company associated with the vendor.
     */
    private String companyPhoneNumber;

    /**
     * Website URL of the company associated with the vendor.
     */
    @Pattern(regexp = "^(https?://)?(www\\.)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}([/a-zA-Z0-9-._~:?#[\\\\]@!$&'()*+,;=%]*)?$", message = "Invalid website format.")
    private String website;

    /**
     * Street address of the vendor.
     */
    private String address;

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

    private String remitAddress;

    private Integer remitStateId;

    private Integer remitCountryId;

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