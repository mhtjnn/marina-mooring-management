package com.marinamooringmanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a request for country data.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CountryRequestDto {

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

