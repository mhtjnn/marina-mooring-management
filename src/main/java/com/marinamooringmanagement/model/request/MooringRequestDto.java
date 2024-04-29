package com.marinamooringmanagement.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

/**
 * Represents a mooring request data transfer object (DTO) used for creating or updating mooring entities.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MooringRequestDto implements Serializable {

    private static final long serialVersionUID = 550268063035509L;

    /**
     * Unique identifier for the mooring.
     */
    private Integer id;

    /**
     * Name of the mooring
     */
    private String mooringName;

    /**
     * Name of the customer associated with the mooring.
     */
    private String customerName;

    /**
     * Mooring number assigned to the mooring.
     */
    @NotNull(message = "Mooring number cannot be null")
    private String mooringNumber;

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
     * Condition of the eye related to the mooring.
     */
    private String conditionOfEye;

    /**
     * Condition of the top chain related to the mooring.
     */
    private String topChainCondition;

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
}