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
public class SizeOfWeightDto extends BaseDto {

    private String weight;

    private String unitType;

}
