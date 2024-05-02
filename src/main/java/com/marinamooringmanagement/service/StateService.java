package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.dto.StateDto;
import com.marinamooringmanagement.model.request.StateSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;

/**
 * This interface defines the contract for managing state-related operations.
 */
public interface StateService {

    /**
     * Fetches states based on the provided search criteria.
     *
     * @param stateSearchRequest An instance of {@code StateSearchRequest} containing the search criteria.
     * @return A {@code BasicRestResponse} object containing the response data, including the list of states matching the search criteria.
     * @throws IllegalArgumentException if {@code stateSearchRequest} is {@code null}.
     */
    BasicRestResponse fetchStates(StateSearchRequest stateSearchRequest);

    /**
     * Saves a new state into the database.
     *
     * @param dto The {@code StateDto} object containing the details of the state to be saved.
     * @return A {@code BasicRestResponse} object indicating the status of the operation.
     */
    BasicRestResponse saveState(StateDto dto);

    /**
     * Updates an existing state in the database.
     *
     * @param dto       The {@code StateDto} object containing the updated details of the state.
     * @param stateId   The ID of the state to be updated.
     * @return A {@code BasicRestResponse} object indicating the status of the operation.
     */
    BasicRestResponse updateState(StateDto dto, Integer stateId);

    /**
     * Deletes a state from the database.
     *
     * @param id The ID of the state to be deleted.
     * @return A {@code BasicRestResponse} object indicating the status of the operation.
     */
    BasicRestResponse deleteState(Integer id);
}
