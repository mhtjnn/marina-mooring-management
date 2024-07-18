package com.marinamooringmanagement.model.response.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) representing a response for country data.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CountryResponseDto implements Serializable {

    private static final long serialVersionUID = 550266303507976L;

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

