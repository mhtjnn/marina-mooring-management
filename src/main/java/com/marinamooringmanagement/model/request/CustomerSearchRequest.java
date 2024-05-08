package com.marinamooringmanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerSearchRequest extends BaseSearchRequest{

    private String customerName;
    /**
     * The customer ID.
     */
    private String customerId;

    /**
     * The phone number of the customer.
     */
    private String phone;

    /**
     * The email address of the customer.
     */
    private String emailAddress;

    /**
     * The street and house number of the customer's address.
     */
    private String streetHouse;

    /**
     * The Apt/Suite of the customer's address.
     */
    private String aptSuite;
    /**
     * The state of the customer's address.
     */
    private String state;
    /**
     * The country of the customer's address.
     */
    private String country;
    /**
     * The zip code of the customer's address.
     */
    private String zipCode;
}
