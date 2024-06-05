package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.VendorDto;
import com.marinamooringmanagement.model.entity.Vendor;
import com.marinamooringmanagement.model.request.VendorRequestDto;
import com.marinamooringmanagement.model.response.VendorResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper interface for mapping between Vendor entities and Vendor DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VendorMapper {

    /**
     * Maps a VendorDto to a Vendor entity.
     *
     * @param vendor The target Vendor entity to map to.
     * @param dto    The source VendorDto object to map from.
     * @return The mapped Vendor entity.
     */
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "remitState", ignore = true)
    @Mapping(target = "remitCountry", ignore = true)
    Vendor mapToVendor(@MappingTarget Vendor vendor, VendorDto dto);

    /**
     * Maps a Vendor entity to a VendorDto.
     *
     * @param dto    The target VendorDto object to map to.
     * @param vendor The source Vendor entity to map from.
     * @return The mapped VendorDto object.
     */
    @Mapping(target = "stateDto", ignore = true)
    @Mapping(target = "countryDto", ignore = true)
    @Mapping(target = "remitStateDto", ignore = true)
    @Mapping(target = "remitCountryDto", ignore = true)
    VendorDto mapToVendorDto(@MappingTarget VendorDto dto, Vendor vendor);

    /**
     * Maps a Vendor entity to a VendorResponseDto.
     *
     * @param dto    The target VendorResponseDto object to map to.
     * @param vendor The source Vendor entity to map from.
     * @return The mapped VendorResponseDto object.
     */
    @Mapping(target = "stateResponseDto", ignore = true)
    @Mapping(target = "countryResponseDto", ignore = true)
    @Mapping(target = "remitStateResponseDto", ignore = true)
    @Mapping(target = "remitCountryResponseDto", ignore = true)
    VendorResponseDto mapToVendorResponseDto(@MappingTarget VendorResponseDto dto, Vendor vendor);

    /**
     * Maps a VendorRequestDto to a Vendor entity.
     *
     * @param vendor The target Vendor entity to map to.
     * @param dto    The source VendorRequestDto object to map from.
     * @return The mapped Vendor entity.
     */
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "remitState", ignore = true)
    @Mapping(target = "remitCountry", ignore = true)
    @Mapping(target = "user", ignore = true)
    Vendor mapToVendor(@MappingTarget Vendor vendor, VendorRequestDto dto);
}