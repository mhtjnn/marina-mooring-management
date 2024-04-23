package com.marinamooringmanagement.model.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a response for customer details,
 * encapsulating information such as customer name, contact details, and address.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponseDto {
    /**
     * The unique identifier for the customer response.
     */
    private Integer id;
    /**
     * The name of the customer.
     */
    private String customerName;
    /**
     * The identification number or code of the customer.
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
     * The pin code of the customer's address.
     */
    private String zipCode;

}
