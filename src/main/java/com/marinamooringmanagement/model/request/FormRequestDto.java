package com.marinamooringmanagement.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormRequestDto {

    private String formName;

    private String fileName;

    private String encodedFormData;

}
