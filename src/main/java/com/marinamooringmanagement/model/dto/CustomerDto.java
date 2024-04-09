package com.marinamooringmanagement.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) class representing a Customer.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDto extends BaseDto {
    /**
     * The unique identifier of the customer.
     */
    private Integer id;
    /**
     * The name of the customer.
     */
    @NotNull(message = "Customer Name can't be blank")
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
    @Email(message = "Email is not Valid")
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
    @NotEmpty(message = "state can't be empty")
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
