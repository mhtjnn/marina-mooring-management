package com.marinamooringmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a state.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StateDto extends BaseDto {

    private static final long serialVersionUID = 55026806303507976L;

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
