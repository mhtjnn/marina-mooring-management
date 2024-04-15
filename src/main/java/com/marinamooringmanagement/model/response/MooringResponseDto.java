package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * The {@code MooringResponseDto} class represents a Data Transfer Object (DTO) for mooring response.
 * This class is used to transfer data related to mooring information, including details about the mooring,
 * customer, and boat associated with it.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MooringResponseDto implements Serializable {

    private static final long serialVersionUID = 550268063035079L;

    /**
     * The unique identifier for the mooring.
     */
    private Integer id;

    /**
     * The name of the mooring.
     */
    private String mooringName;

    /**
     * The name of the customer who owns or uses the mooring.
     */
    private String customerName;

    /**
     * The unique number assigned to the mooring.
     */
    private String mooringNumber;

    /**
     * The harbor where the mooring is located.
     */
    private String harbor;

    /**
     * The depth of the water at the mooring location.
     */
    private String waterDepth;

    /**
     * The GPS coordinates of the mooring location.
     */
    private String gpsCoordinates;

    /**
     * The name of the boat associated with the mooring.
     */
    private String boatName;

    /**
     * The size of the boat associated with the mooring.
     */
    private String boatSize;

    /**
     * The type of the boat associated with the mooring.
     */
    private String boatType;

    /**
     * The weight of the boat associated with the mooring.
     */
    private String boatWeight;

    /**
     * The size of the weight used for the mooring.
     */
    private String sizeOfWeight;

    /**
     * The type of the weight used for the mooring.
     */
    private String typeOfWeight;

    /**
     * The condition of the eye used for the mooring.
     */
    private String conditionOfEye;

    /**
     * The condition of the top chain of the mooring.
     */
    private String topChainCondition;

    /**
     * The condition of the bottom chain of the mooring.
     */
    private String bottomChainCondition;

    /**
     * The condition of the shackle or swivel of the mooring.
     */
    private String shackleSwivelCondition;

    /**
     * The condition of the pennant of the mooring.
     */
    private String pennantCondition;

    /**
     * The depth at mean high water at the mooring location.
     */
    private Integer depthAtMeanHighWater;
}
