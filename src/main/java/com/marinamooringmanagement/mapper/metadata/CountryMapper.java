package com.marinamooringmanagement.mapper.metadata;

import com.marinamooringmanagement.model.dto.metadata.CountryDto;
import com.marinamooringmanagement.model.entity.metadata.Country;
import com.marinamooringmanagement.model.request.CountryRequestDto;
import com.marinamooringmanagement.model.response.CountryResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper interface for mapping between {@link Country} entity and its corresponding DTOs.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CountryMapper {

    /**
     * Maps a {@link Country} entity to a {@link CountryDto}.
     *
     * @param dto     The target {@link CountryDto} to map to.
     * @param country The {@link Country} entity to map from.
     * @return The mapped {@link CountryDto}.
     */
    CountryDto mapToCountryDto(@MappingTarget CountryDto dto, Country country);

    /**
     * Maps data from a {@link CountryRequestDto} to a {@link Country} entity.
     *
     * @param entity            The target {@link Country} entity to map to.
     * @param countryRequestDto The {@link CountryRequestDto} containing data to map from.
     */
    void mapToCountry(@MappingTarget Country entity, CountryRequestDto countryRequestDto);

    /**
     * Maps data from a {@link CountryDto} to a {@link Country} entity.
     *
     * @param entity     The target {@link Country} entity to map to.
     * @param countryDto The {@link CountryDto} containing data to map from.
     */
    void mapToCountry(@MappingTarget Country entity, CountryDto countryDto);

    /**
     * Maps a {@link Country} entity to a {@link CountryResponseDto}.
     *
     * @param dto     The target {@link CountryResponseDto} to map to.
     * @param country The {@link Country} entity to map from.
     * @return The mapped {@link CountryResponseDto}.
     */
    CountryResponseDto mapToCountryResponseDto(@MappingTarget CountryResponseDto dto, Country country);
}

