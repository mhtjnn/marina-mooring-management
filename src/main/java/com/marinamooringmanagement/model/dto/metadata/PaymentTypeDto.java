package com.marinamooringmanagement.model.dto.metadata;

import com.marinamooringmanagement.model.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTypeDto extends BaseDto {

    private Integer id;

    private String type;

    private String description;

}
