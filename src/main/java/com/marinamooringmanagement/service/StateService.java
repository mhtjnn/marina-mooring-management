package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.dto.metadata.StateDto;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;

/**
 * This interface defines the contract for managing state-related operations.
 */
public interface StateService {

    /**
     * Fetches a list of states based on the provided search request parameters.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @return a BasicRestResponse containing the results of the state search.
     */
    BasicRestResponse fetchStates(final BaseSearchRequest baseSearchRequest);

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
