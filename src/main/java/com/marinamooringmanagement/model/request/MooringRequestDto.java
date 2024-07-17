package com.marinamooringmanagement.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

    @NotNull(message = "Mooring number cannot be blank")
    private String mooringNumber;

    /**
     * Name of the customer associated with the mooring.
     */
    private Integer customerId;

    /**
     * Harbor where the mooring is located.
     */
    private String harborOrArea;

    /**
     * GPS coordinates of the mooring.
     */
    private String gpsCoordinates;

    private String installBottomChainDate;

    private String installTopChainDate;

    private String installConditionOfEyeDate;

    private String inspectionDate;

    private Boolean addDock;

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
    private Integer boatSize;

    /**
     * Type of the boat associated with the mooring.
     */
    private Integer boatTypeId;

    /**
     * Weight of the boat associated with the mooring.
     */
    private Integer boatWeight;

    /**
     * Size unit of the boat weight.
     */
    private Integer sizeOfWeight;

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
    private String pendantCondition;

    /**
     * Depth at mean high water at the mooring location.
     */
    private Integer depthAtMeanHighWater;

    /**
     * The status of the mooring.
     */
    private Integer statusId;

    private List<ImageRequestDto> imageRequestDtoList;

    private Integer serviceAreaId;
}
