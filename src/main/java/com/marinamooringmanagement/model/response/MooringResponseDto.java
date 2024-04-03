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

    private String mooringNumber;

    private String ownerName;

    private String harbor;

    private String waterDepth;

    private String gpsCoordinates;

    private String boatName;

    private String boatSize;

    private String boatType;

    private String boatWeight;

    private String conditionOfEye;

    private String bottomChainCondition;

    private String topChainCondition;

    private String shackleSwivelCondition;

    private String pennantCondition;
}
