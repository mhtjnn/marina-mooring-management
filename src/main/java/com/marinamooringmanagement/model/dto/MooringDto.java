package com.marinamooringmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MooringDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 5502680630355079L;

    /**
     * Unique identifier for the mooring.
     */
    private Integer id;

    private String customerName;

    /**
     * Mooring number assigned to the mooring.
     */
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
     * The size of the weight used or required for the service.
     * This could be expressed in units relevant to the service context, such as kilograms, pounds, or another suitable measurement.
     */
    private String sizeOfWeight;
    /**
     * The type of weight involved in the service.
     * This might refer to categories such as "permanent", "temporary", "heavy", or "light",
     * depending on the classification system used at the marina.
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
     * The depth of water at mean high water (MHW), typically measured in meters or feet.
     * This value is crucial for determining safe mooring depths and for navigational safety purposes.
     */
    private Integer depthAtMeanHighWater;
}
