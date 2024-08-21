package com.marinamooringmanagement.model.dto.config;

import com.marinamooringmanagement.model.dto.BaseDto;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigDto extends BaseDto {

    private Integer id;

    private Boolean isMarina;

    private Boolean isBoatyard;

}
