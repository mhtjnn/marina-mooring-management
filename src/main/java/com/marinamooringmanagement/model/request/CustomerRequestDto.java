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

    @NotNull(message = "First Name cannot be null")
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    @Pattern(regexp = "^.{3,}$", message = "Last name should contain at least 3 character")
    private String lastName;

    /**
     * The email address of the customer.
     */
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email address format.")
    private String emailAddress;

    /**
     * The phone number of the customer.
     */
    private String phone;

    private String notes;

    private String address;
    /**
     * The state or region of the customer's address.
     */
    private Integer stateId;

    /**
     * The country of the customer's address.
     */
    private Integer countryId;

    private List<ImageRequestDto> imageRequestDtoList;

    /**
     * The zip code or postal code of the customer's address.
     */
    private String zipCode;

    private Integer customerTypeId;

    @Valid
    private List<MooringRequestDto> mooringRequestDtoList;
}