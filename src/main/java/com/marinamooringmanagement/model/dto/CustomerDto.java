package com.marinamooringmanagement.model.dto;

import com.marinamooringmanagement.model.entity.Mooring;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    private List<MooringDto> mooringDtoList;
}