package com.marinamooringmanagement.model.dto.metadata;

import com.marinamooringmanagement.model.dto.BaseDto;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceAreaTypeDto extends BaseDto {

    private Integer id;

    private String type;

    private String description;

}
