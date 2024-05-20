package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.dto.CountryDto;

import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;

/**
 * This interface defines the contract for managing country-related operations.
 */
public interface CountryService {

    /**
     * Fetches a list of countries based on the provided search request parameters.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @return a BasicRestResponse containing the results of the country search.
     */
    BasicRestResponse fetchCountries(final BaseSearchRequest baseSearchRequest);

    /**
     * Saves a new country into the database.
     *
     * @param dto The {@code CountryDto} object containing the details of the country to be saved.
     * @return A {@code BasicRestResponse} object indicating the status of the operation.
     */
    BasicRestResponse saveCountry(CountryDto dto);

    /**
     * Updates an existing country in the database.
     *
     * @param dto       The {@code CountryDto} object containing the updated details of the country.
     * @param countryId The ID of the country to be updated.
     * @return A {@code BasicRestResponse} object indicating the status of the operation.
     */
    BasicRestResponse updateCountry(CountryDto dto, Integer countryId);

    /**
     * Deletes a country from the database.
     *
     * @param id The ID of the country to be deleted.
     * @return A {@code BasicRestResponse} object indicating the status of the operation.
     */
    BasicRestResponse deleteCountry(Integer id);
}

