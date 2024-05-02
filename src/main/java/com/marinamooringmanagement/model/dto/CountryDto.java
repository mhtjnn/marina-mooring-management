package com.marinamooringmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CountryDto extends BaseDto {

    private static final long serialVersionUID = 550206303507976L;

    private Integer id;

    private String name;

    private String label;
}