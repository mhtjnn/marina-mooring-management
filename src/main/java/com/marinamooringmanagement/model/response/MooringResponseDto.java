package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.dto.*;
import com.marinamooringmanagement.model.entity.MooringStatus;
import com.marinamooringmanagement.model.entity.TopChainCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

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

    private String mooringId;

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

    private String boatyardName;

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
    private BoatTypeDto boatType;

    /**
     * The weight of the boat associated with the mooring.
     */
    private String boatWeight;

    /**
     * The size of the weight used for the mooring.
     */
    private SizeOfWeightDto sizeOfWeight;

    /**
     * The type of the weight used for the mooring.
     */
    private TypeOfWeightDto typeOfWeight;

    /**
     * The condition of the eye used for the mooring.
     */
    private EyeConditionDto eyeCondition;

    /**
     * The condition of the top chain of the mooring.
     */
    private TopChainConditionDto topChainCondition;

    /**
     * The condition of the bottom chain of the mooring.
     */
    private BottomChainConditionDto bottomChainCondition;

    /**
     * The condition of the shackle or swivel of the mooring.
     */
    private ShackleSwivelConditionDto shackleSwivelCondition;

    /**
     * The condition of the pennant of the mooring.
     */
    private PennantConditionDto pennantCondition;

    /**
     * The depth at mean high water at the mooring location.
     */
    private Integer depthAtMeanHighWater;

    /**
     * The status of mooring
     */
    private MooringStatusDto mooringStatus;

    private Integer customerId;

    private Integer userId;
}
