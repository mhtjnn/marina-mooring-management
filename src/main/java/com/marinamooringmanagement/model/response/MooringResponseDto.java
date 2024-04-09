package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MooringResponseDto implements Serializable {

    private static final long serialVersionUID = 550268063035079L;

    private Integer id;

    private String customerName;

    private String mooringNumber;

    private String harbor;

    private String waterDepth;

    private String gpsCoordinates;

    private String boatName;

    private String boatSize;

    private String boatType;

    private String boatWeight;

    private String sizeOfWeight;

    private String typeOfWeight;

    private String conditionOfEye;

    private String topChainCondition;

    private String bottomChainCondition;

    private String shackleSwivelCondition;

    private String pennantCondition;

    private Integer depthAtMeanHighWater;
}
