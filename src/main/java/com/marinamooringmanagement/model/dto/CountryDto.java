package com.marinamooringmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a country.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CountryDto extends BaseDto {

    private static final long serialVersionUID = 550206303507976L;

    /**
     * The unique identifier for the country.
     */
    private Integer id;

    /**
     * The name of the country.
     */
    private String name;

    /**
     * The label of the country.
     */
    private String label;
}
