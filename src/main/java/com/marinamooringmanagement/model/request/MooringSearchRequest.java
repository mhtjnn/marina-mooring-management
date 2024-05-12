package com.marinamooringmanagement.model.request;

import lombok.*;

/**
 * Represents a search request for querying mooring information based on specified criteria.
 *
 * <p>This class extends {@code BaseSearchRequest} and includes additional fields to define search criteria for moorings.
 *
 * @see BaseSearchRequest
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MooringSearchRequest extends BaseSearchRequest {

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