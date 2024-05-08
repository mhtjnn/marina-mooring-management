package com.marinamooringmanagement.model.dto;

import com.marinamooringmanagement.model.entity.Boatyard;
import com.marinamooringmanagement.model.entity.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a mooring data transfer object (DTO) used for transferring mooring-related information.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MooringDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 5502680630355079L;

    private Integer id;

    /**
     * Name of the mooring.
     */
    private String mooringName;

    private String mooringId;

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
     * Name of the boatyard associated with the mooring.
     */
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

    /**
     * Status of the mooring.
     */
    private String status;

    private Boatyard boatyard;

    private Customer customer;
}
