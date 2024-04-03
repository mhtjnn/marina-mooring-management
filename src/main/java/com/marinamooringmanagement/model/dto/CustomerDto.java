package com.marinamooringmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) class representing a Customer.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerDto {
    /**
     * The unique identifier of the customer.
     */
    private Integer id;
    /**
     * The name of the customer.
     */
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
     * The sector or block of the customer's address.
     */
    private String sectorBlock;
    /**
     * The state of the customer's address.
     */
    private String state;
    /**
     * The country of the customer's address.
     */
    private String country;

    /**
     * The pin code of the customer's address.
     */
    private String pinCode;

    /**
     * Additional notes or comments about the customer.
     */
    private String note;


}
