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

    @NotNull(message = "Company name cannot be blank")
    private String companyName;

    private String companyPhoneNumber;

    private String website;

    private String street;

    private String aptSuite;

    private String state;

    private String country;

    private Integer zipCode;

    private String companyEmail;

    private String accountNumber;

    private String firstName;

    private String lastName;

    private String salesRepPhoneNumber;

    private String salesRepEmail;

    private String salesRepNote;

    private boolean primarySalesRep;
}
