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
     * The name of the company. Cannot be null.
     */
    @NotNull(message = "Company name cannot be blank")
    private String companyName;

    /**
     * The phone number of the company.
     */
    private String companyPhoneNumber;

    /**
     * The website URL of the company.
     */
    private String website;

    /**
     * The street address of the company.
     */
    private String street;

    /**
     * The apartment or suite number of the company's address.
     */
    private String aptSuite;

    /**
     * The state or province of the company's address.
     */
    private String state;

    /**
     * The country of the company's address.
     */
    private String country;

    /**
     * The ZIP or postal code of the company's address.
     */
    private Integer zipCode;

    /**
     * The email address of the company.
     */
    private String companyEmail;

    /**
     * The account number associated with the company.
     */
    private String accountNumber;

    /**
     * The first name of the sales representative.
     */
    private String firstName;

    /**
     * The last name of the sales representative.
     */
    private String lastName;

    /**
     * The phone number of the sales representative.
     */
    private String salesRepPhoneNumber;

    /**
     * The email address of the sales representative.
     */
    private String salesRepEmail;

    /**
     * Additional notes about the sales representative.
     */
    private String salesRepNote;

    /**
     * Indicates if the sales representative is the primary contact.
     */
    private boolean primarySalesRep;
}