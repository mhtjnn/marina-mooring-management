package com.marinamooringmanagement.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a customer request, encapsulating details
 * necessary for managing customer information and requests within the system.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequestDto {
    private Integer id;

    /**
     * The customer's identification number or code.
     */
    private String customerId;

    private String customerName;

    /**
     * The email address of the customer.
     */
    @NotNull(message = "Email cannot be null")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email address format.")
    private String emailAddress;

    /**
     * The phone number of the customer.
     */
    private String phone;

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
    private Integer stateId;

    /**
     * The country of the customer's address.
     */
    private Integer countryId;

    /**
     * The zip code or postal code of the customer's address.
     */
    private String zipCode;

    @Valid
    private List<MooringRequestDto> mooringRequestDtoList;
}