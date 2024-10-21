package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.dto.metadata.CustomerTypeDto;
import com.marinamooringmanagement.model.dto.ImageDto;
import com.marinamooringmanagement.model.response.metadata.CountryResponseDto;
import com.marinamooringmanagement.model.response.metadata.StateResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing a response for customer details,
 * encapsulating information such as customer name, contact details, and address.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponseDto implements Serializable {

    private static final long serialVersionUID = 5526863507409L;

    /**
     * The unique identifier for the customer response.
     */
    private Integer id;
    /**
     * The name of the customer.
     */
    private String firstName;

    private String lastName;
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

    private String notes;

    private String address;

    private String city;

    /**
     * The state or region of the customer's address.
     */
    private StateResponseDto stateResponseDto;
    /**
     * The country of the customer's address.
     */
    private CountryResponseDto countryResponseDto;
    /**
     * The pin code of the customer's address.
     */
    private String zipCode;

    private CustomerTypeDto customerTypeDto;

    private Integer userId;

    private List<MooringResponseDto> mooringResponseDtoList;

    private List<ImageDto> imageDtoList;

    private QuickbookCustomerResponseDto quickbookCustomerResponseDto;
}
