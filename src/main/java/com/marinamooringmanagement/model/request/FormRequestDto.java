package com.marinamooringmanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormRequestDto {

    private Integer id;

    private String formName;

    private String fileName;

    private String encodedFormData;

    private Integer parentFormId;
}
