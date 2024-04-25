package com.marinamooringmanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a customer request, encapsulating details
 * necessary for managing customer information and requests within the system.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequestDto {
    /**
     * The unique identifier for the customer request.
     */
    private Integer id;
    /**
     * The name of the customer making the request.
     */
    private String customerName;
    /**
     * The customer's identification number or code.
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
     * The street and house details of the customer's address.
     */
    private String streetHouse;
    /**
     * The Apt/Suite of the customer's address.
     */
    private String aptSuite;
    /**
     * The state or region of the customer's address.
     */
    private String state;
    /**
     * The country of the customer's address.
     */
    private String country;
    /**
     * The zip code or postal code of the customer's address.
     */
    private String zipCode;

}