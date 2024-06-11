package com.marinamooringmanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a request for state data.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StateRequestDto {

    /**
     * The unique identifier for the state.
     */
    private Integer id;

    /**
     * The name of the state.
     */
    private String name;

    /**
     * The label of the state.
     */
    private String label;
}

