package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.CountryDto;
import com.marinamooringmanagement.model.entity.Country;
import com.marinamooringmanagement.model.request.CountryRequestDto;
import com.marinamooringmanagement.model.response.CountryResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CountryMapper {

    CountryDto mapToCountryDto(@MappingTarget CountryDto dto, Country country);

    void mapToCountry(@MappingTarget Country entity, CountryRequestDto countryRequestDto);

    void mapToCountry(@MappingTarget Country entity, CountryDto countryDto);

    CountryResponseDto mapToCountryResponseDto(@MappingTarget CountryResponseDto dto, Country country);
}
