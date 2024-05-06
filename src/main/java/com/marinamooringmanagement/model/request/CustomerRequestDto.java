package com.marinamooringmanagement.model.request;

import jakarta.validation.constraints.NotNull;
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
     * The email address of the customer.
     */
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
    private String state;
    /**
     * The country of the customer's address.
     */
    private String country;
    /**
     * The zip code or postal code of the customer's address.
     */
    private String zipCode;

    /**
     * Name of the mooring
     */
    @NotNull(message = "Mooring ID cannot be null")
    private String mooringId;

    /**
     * Harbor where the mooring is located.
     */
    private String harbor;

    /**
     * Water depth at the mooring location.
     */
    private String waterDepth;

    /**
     * GPS coordinates of the mooring.
     */
    private String gpsCoordinates;

    /**
     * Name of the boatyard associated with the mooring.
     */
    @NotNull(message = "Boatyard name cannot be blank")
    private String boatyardName;

    /**
     * Name of the boat associated with the mooring.
     */
    private String boatName;

    /**
     * Size of the boat associated with the mooring.
     */
    private String boatSize;

    /**
     * Type of the boat associated with the mooring.
     */
    private String boatType;

    /**
     * Weight of the boat associated with the mooring.
     */
    private String boatWeight;

    /**
     * Size unit of the boat weight.
     */
    private String sizeOfWeight;

    /**
     * Type of the boat weight.
     */
    private String typeOfWeight;

    /**
     * Condition of the top chain related to the mooring.
     */
    private String topChainCondition;

    /**
     * Condition of the eye related to the mooring.
     */
    private String conditionOfEye;

    /**
     * Condition of the bottom chain related to the mooring.
     */
    private String bottomChainCondition;

    /**
     * Condition of the shackle/swivel related to the mooring.
     */
    private String shackleSwivelCondition;

    /**
     * Condition of the pennant related to the mooring.
     */
    private String pennantCondition;

    /**
     * Depth at mean high water at the mooring location.
     */
    private Integer depthAtMeanHighWater;

    /**
     * The status of the mooring.
     */
    private String status;

    private String gpsCoordinate;
}