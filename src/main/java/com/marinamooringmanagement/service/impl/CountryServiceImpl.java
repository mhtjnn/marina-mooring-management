package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.mapper.metadata.CountryMapper;
import com.marinamooringmanagement.model.dto.metadata.CountryDto;
import com.marinamooringmanagement.model.entity.metadata.Country;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.CountryResponseDto;
import com.marinamooringmanagement.repositories.CountryRepository;
import com.marinamooringmanagement.service.CountryService;
import com.marinamooringmanagement.utils.SortUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing countries.
 * This class provides methods for fetching, saving, updating, and deleting country data.
 */
@Service
public class CountryServiceImpl implements CountryService {

    private static final Logger log = LoggerFactory.getLogger(CountryServiceImpl.class);

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CountryMapper countryMapper;

    @Autowired
    private SortUtils sortUtils;

    /**
     * Fetches a list of countries based on the provided search request parameters.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @return a BasicRestResponse containing the results of the country search.
     */
    @Override
    public BasicRestResponse fetchCountries(final BaseSearchRequest baseSearchRequest) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("fetch all countries");
            final Pageable p = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    sortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir()));
            final Page<Country> countryList = countryRepository.findAll(p);

            final List<CountryResponseDto> countryResponseDtoList = countryList.stream()
                    .map(country -> countryMapper.mapToCountryResponseDto(CountryResponseDto.builder().build(), country))
                    .toList();

            response.setMessage("Countries fetched Successfully");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(countryResponseDtoList);
        } catch (Exception e) {
            response.setMessage("Error Occurred while fetching country from the database");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setErrorList(List.of(e.getMessage()));
        }
        return response;
    }

    /**
     * Saves a new country.
     *
     * @param countryDto The DTO containing the country data to be saved.
     * @return A BasicRestResponse indicating the outcome of the save operation.
     */
    @Override
    public BasicRestResponse saveCountry(CountryDto countryDto) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Country country = Country.builder().build();
            final Optional<Country> optionalCountry = countryRepository.findByName(countryDto.getName());

            if (optionalCountry.isPresent()) {
                log.info("Country already present in DB");
                throw new RuntimeException("Country already present in DB");
            }

            log.info("Saving country in DB");
            performSave(countryDto, country, null);

            response.setMessage("Country saved successfully");
            response.setStatus(HttpStatus.CREATED.value());

        } catch (Exception e) {
            response.setMessage("Error Occurred while saving country");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setErrorList(List.of(e.getMessage()));
        }
        return response;
    }

    /**
     * Updates an existing country.
     *
     * @param countryDto The DTO containing the updated country data.
     * @param countryId  The ID of the country to be updated.
     * @return A BasicRestResponse indicating the outcome of the update operation.
     */
    @Override
    public BasicRestResponse updateCountry(CountryDto countryDto, Integer countryId) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Optional<Country> optionalCountry = countryRepository.findById(countryId);

            if (optionalCountry.isEmpty()) {
                log.info("Country not present in DB");
                throw new RuntimeException("Country not present in DB");
            }

            final Country country = optionalCountry.get();

            log.info("Updating country in DB");
            performSave(countryDto, country, countryId);

            response.setMessage("Country updated successfully");
            response.setStatus(HttpStatus.CREATED.value());

        } catch (Exception e) {
            response.setMessage("Error Occurred while updating country");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setErrorList(List.of(e.getMessage()));
        }
        return response;
    }

    /**
     * Deletes a country.
     *
     * @param id The ID of the country to be deleted.
     * @return A BasicRestResponse indicating the outcome of the delete operation.
     */
    @Override
    public BasicRestResponse deleteCountry(Integer id) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("Deleting country with ID {}", id);
            countryRepository.deleteById(id);
            response.setMessage("Country Deleted Successfully!!!");
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            response.setMessage("Error occurred while deleting the country");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setErrorList(List.of(e.getMessage()));
        }
        return response;
    }

    /**
     * Performs the save operation for a country.
     *
     * @param countryDto The DTO containing the country data.
     * @param country    The country entity to be saved.
     * @param userId     The ID of the user performing the operation.
     * @return The saved country entity.
     */
    public Country performSave(final CountryDto countryDto, final Country country, final Integer userId) {
        try {
            countryMapper.mapToCountry(country, countryDto);
            country.setLastModifiedDate(new Date(System.currentTimeMillis()));
            return countryRepository.save(country);
        } catch (Exception e) {
            log.error("Error occurred during perform save method: {}", e.getLocalizedMessage());
            throw new RuntimeException("Error occurred during perform save method", e);
        }
    }
}