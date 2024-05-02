package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.dto.CountryDto;
import com.marinamooringmanagement.model.request.CountryRequestDto;
import com.marinamooringmanagement.model.request.CountrySearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;

public interface CountryService {
    BasicRestResponse fetchCountries(CountrySearchRequest countrySearchRequest);

    BasicRestResponse saveCountry(CountryDto dto);

    BasicRestResponse updateCountry(CountryDto dto, Integer countryId);

    BasicRestResponse deleteCountry(Integer id);
}
