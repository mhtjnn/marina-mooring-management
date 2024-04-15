package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The {@code VendorResponseDto} class represents a Data Transfer Object (DTO) for vendor response.
 * This class is used to transfer vendor data, including information about the company, contact details,
 * and sales representative information.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VendorResponseDto {

    private static final long serialVersionUID = 5502680635079L;

    /**
     * The unique identifier for the vendor.
     */
    private Integer id;

    /**
     * The name of the company.
     */
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
     * The apartment or suite number of the company, if applicable.
     */
    private String aptSuite;

    /**
     * The state in which the company is located.
     */
    private String state;

    /**
     * The country in which the company is located.
     */
    private String country;

    /**
     * The zip code of the company's address.
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
     * Notes about the sales representative.
     */
    private String salesRepNote;

    /**
     * Indicates whether the sales representative is the primary representative.
     */
    private boolean primarySalesRep;
}
