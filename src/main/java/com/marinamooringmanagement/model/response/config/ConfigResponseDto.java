package com.marinamooringmanagement.model.response.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigResponseDto {

    private Integer id;

    private Boolean isMarina;

    private Boolean isBoatyard;

}
