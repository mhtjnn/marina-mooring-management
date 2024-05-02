package com.marinamooringmanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a search request for state data.
 * This class extends BaseSearchRequest and includes state-specific search parameters.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StateSearchRequest extends BaseSearchRequest {

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

