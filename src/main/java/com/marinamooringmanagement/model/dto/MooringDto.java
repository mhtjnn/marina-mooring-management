package com.marinamooringmanagement.model.dto;

import com.marinamooringmanagement.model.dto.metadata.*;
import com.marinamooringmanagement.model.entity.ServiceArea;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
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
     * Mooring number assigned to the mooring.
     */
    private String mooringNumber;

    /**
     * Name of the customer associated with the mooring.
     */
    private String customerName;

    /**
     * Harbor where the mooring is located.
     */
    private String harborOrArea;

    /**
     * GPS coordinates of the mooring.
     */
    private String gpsCoordinates;

    private Date installBottomChainDate;

    private Date installTopChainDate;

    private Date installConditionOfEyeDate;

    private Date inspectionDate;

    private String boatId;

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
    private BoatTypeDto boatType;

    /**
     * Weight of the boat associated with the mooring.
     */
    private String boatWeight;

    /**
     * Size unit of the boat weight.
     */
    private Integer sizeOfWeight;

    /**
     * Type of the boat weight.
     */
    private TypeOfWeightDto typeOfWeight;

    /**
     * Condition of the eye related to the mooring.
     */
    private EyeConditionDto eyeCondition;

    /**
     * Condition of the top chain related to the mooring.
     */
    private TopChainConditionDto topChainCondition;

    /**
     * Condition of the bottom chain related to the mooring.
     */
    private BottomChainConditionDto bottomChainCondition;

    /**
     * Condition of the shackle/swivel related to the mooring.
     */
    private ShackleSwivelConditionDto shackleSwivelCondition;

    /**
     * Condition of the pennant related to the mooring.
     */
    private String pendantCondition;

    /**
     * Depth at mean high water at the mooring location.
     */
    private Integer depthAtMeanHighWater;

    /**
     * Status of the mooring.
     */
    private String status;

    private CustomerDto customerDto;

    private UserDto userDto;

    private BoatyardDto boatyardDto;

    private List<ImageDto> imageDtoList;

    private ServiceAreaDto serviceAreaDto;
}
