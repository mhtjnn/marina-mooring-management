package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormResponseDto {

    private Integer id;

    private String submittedDate;

    private String submittedBy;

    private String formName;

    private String fileName;

    private byte[] formData;

    private UserResponseDto userResponseDto;

}
