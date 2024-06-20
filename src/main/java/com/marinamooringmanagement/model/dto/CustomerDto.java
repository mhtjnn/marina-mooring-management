package com.marinamooringmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object (DTO) class representing a Customer.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CustomerDto extends BaseDto {

    private Integer id;

    /**
     * The customer ID.
     */
    private String customerId;

    private String firstName;

    private String lastName;

    /**
     * The phone number of the customer.
     */
    private String phone;

    /**
     * The email address of the customer.
     */
    private String emailAddress;

    private String note;

    private byte[] image;

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
    private StateDto state;
    /**
     * The country of the customer's address.
     */
    private CustomerDto country;
    /**
     * The zip code of the customer's address.
     */
    private String zipCode;

    private CustomerTypeDto customerTypeDto;

    private List<MooringDto> mooringDtoList;

    private UserDto userDto;
}