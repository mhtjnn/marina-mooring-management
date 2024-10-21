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
public class VoiceMEMOResponseDto implements Serializable {

    private static final long serialVersionUID = 55026806303550349L;

    private Integer id;

    private String name;

    private String encodedData;

    private WorkOrderResponseDto workOrderResponseDto;

    private UserResponseDto userResponseDto;
}