package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.mapper.ServiceAreaMapper;
import com.marinamooringmanagement.model.dto.metadata.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MooringDueServiceResponseDto implements Serializable {

    private static final long serialVersionUID = 556726806305079L;

    /**
     * The unique identifier for the mooring.
     */
    private Integer id;

    /**
     * The unique number assigned to the mooring.
     */
    private String mooringNumber;

    /**
     * The harbor where the mooring is located.
     */
    private String harborOrArea;

    /**
     * The GPS coordinates of the mooring location.
     */
    private String gpsCoordinates;

    private String installBottomChainDate;

    private String installTopChainDate;

    private String installConditionOfEyeDate;

    private String inspectionDate;

    private ServiceAreaResponseDto serviceAreaResponseDto;

    private String boatId;

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
    private Integer sizeOfWeight;

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
    private String pendantCondition;

    /**
     * The depth at mean high water at the mooring location.
     */
    private Integer depthAtMeanHighWater;

    private String mainContact;

    /**
     * The status of mooring
     */
    private MooringStatusDto mooringStatus;

    private CustomerResponseDto customerResponseDto;

    private String customerName;

    private Integer userId;

    private BoatyardResponseDto boatyardResponseDto;

    private MooringDueServiceStatusDto mooringDueServiceStatusDto;

    private String mooringServiceDate;

    private Boolean under30;
}
