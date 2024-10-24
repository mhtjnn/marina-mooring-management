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
public class FormResponseDto implements Serializable {

    private static final long serialVersionUID = 552686350796487L;

    private Integer id;

    private String submittedDate;

    private String submittedBy;

    private String formName;

    private String fileName;

    private String encodedData;

    private UserResponseDto userResponseDto;

    private WorkOrderResponseDto workOrderResponseDto;

    private Integer parentFormId;
}
