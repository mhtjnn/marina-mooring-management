package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VendorResponseDto {

    private Integer id;

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
