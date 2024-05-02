package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) representing a response for state data.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StateResponseDto implements Serializable {

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
