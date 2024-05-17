package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.VendorDto;
import com.marinamooringmanagement.model.entity.Vendor;
import com.marinamooringmanagement.model.request.VendorRequestDto;
import com.marinamooringmanagement.model.response.VendorResponseDto;
import org.mapstruct.Mapper;
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
    Vendor mapToVendor(@MappingTarget Vendor vendor, VendorDto dto);

    /**
     * Maps a Vendor entity to a VendorDto.
     *
     * @param dto    The target VendorDto object to map to.
     * @param vendor The source Vendor entity to map from.
     * @return The mapped VendorDto object.
     */
    VendorDto mapToVendorDto(@MappingTarget VendorDto dto, Vendor vendor);

    /**
     * Maps a Vendor entity to a VendorResponseDto.
     *
     * @param dto    The target VendorResponseDto object to map to.
     * @param vendor The source Vendor entity to map from.
     * @return The mapped VendorResponseDto object.
     */
    VendorResponseDto mapToVendorResponseDto(@MappingTarget VendorResponseDto dto, Vendor vendor);

    /**
     * Maps a VendorRequestDto to a Vendor entity.
     *
     * @param vendor The target Vendor entity to map to.
     * @param dto    The source VendorRequestDto object to map from.
     * @return The mapped Vendor entity.
     */
    Vendor mapToVendor(@MappingTarget Vendor vendor, VendorRequestDto dto);
}