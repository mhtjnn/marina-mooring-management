package com.marinamooringmanagement.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    private Integer id;

    @NotNull(message = "Mooring ID cannot be blank")
    private String mooringId;

    /**
     * Name of the customer associated with the mooring.
     */
    private Integer customerId;

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
    private Integer boatyardId;

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
    private Integer boatTypeId;

    /**
     * Weight of the boat associated with the mooring.
     */
    private String boatWeight;

    /**
     * Size unit of the boat weight.
     */
    private Integer sizeOfWeightId;

    /**
     * Type of the boat weight.
     */
    private Integer typeOfWeightId;

    /**
     * Condition of the eye related to the mooring.
     */
    private Integer eyeConditionId;

    /**
     * Condition of the top chain related to the mooring.
     */
    private Integer topChainConditionId;

    /**
     * Condition of the bottom chain related to the mooring.
     */
    private Integer bottomChainConditionId;

    /**
     * Condition of the shackle/swivel related to the mooring.
     */
    private Integer shackleSwivelConditionId;

    /**
     * Condition of the pennant related to the mooring.
     */
    private Integer pennantConditionId;

    /**
     * Depth at mean high water at the mooring location.
     */
    private Integer depthAtMeanHighWater;

    /**
     * The status of the mooring.
     */
    private Integer statusId;
}
