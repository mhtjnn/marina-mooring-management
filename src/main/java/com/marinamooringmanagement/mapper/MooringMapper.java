package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.MooringDto;
import com.marinamooringmanagement.model.entity.Mooring;
import com.marinamooringmanagement.model.request.MooringRequestDto;
import com.marinamooringmanagement.model.response.MooringDueServiceResponseDto;
import com.marinamooringmanagement.model.response.MooringResponseDto;
import com.marinamooringmanagement.model.response.MooringWithGPSCoordinateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper interface for mapping between Mooring entities and Mooring DTOs.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MooringMapper {

    /**
     * Maps a Mooring entity to a MooringDto.
     *
     * @param dto     The target MooringDto object to map to.
     * @param mooring The source Mooring entity to map from.
     * @return The mapped MooringDto object.
     */
    @Mapping(target = "customerDto",ignore = true)
    @Mapping(target = "boatyardDto",ignore = true)
    @Mapping(target = "userDto",ignore = true)
    MooringDto mapToMooringDto(@MappingTarget MooringDto dto, Mooring mooring);

    /**
     * Maps a MooringDto to a Mooring entity.
     *
     * @param mooring The target Mooring entity to map to.
     * @param dto     The source MooringDto object to map from.
     * @return The mapped Mooring entity.
     */
    @Mapping(target = "customer",ignore = true)
    @Mapping(target = "user",ignore = true)
    @Mapping(target = "boatyard",ignore = true)
    Mooring mapToMooring(@MappingTarget Mooring mooring, MooringDto dto);

    /**
     * Maps a Mooring entity to a MooringResponseDto.
     *
     * @param dto     The target MooringResponseDto object to map to.
     * @param mooring The source Mooring entity to map from.
     * @return The mapped MooringResponseDto object.
     */
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "customerName", ignore = true)
    @Mapping(target = "boatyardResponseDto", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "installBottomChainDate", ignore = true)
    @Mapping(target = "installTopChainDate", ignore = true)
    @Mapping(target = "installConditionOfEyeDate", ignore = true)
    @Mapping(target = "inspectionDate", ignore = true)
    @Mapping(target = "customerResponseDto", ignore = true)
    @Mapping(target = "serviceAreaResponseDto", ignore = true)
    MooringResponseDto mapToMooringResponseDto(@MappingTarget MooringResponseDto dto, Mooring mooring);

    /**
     * Maps a MooringRequestDto to a Mooring entity.
     *
     * @param mooring The target Mooring entity to map to.
     * @param dto     The source MooringRequestDto object to map from.
     * @return The mapped Mooring entity.
     */
    @Mapping(target = "boatyard", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "mooringStatus", ignore = true)
    @Mapping(target = "boatType", ignore = true)
    @Mapping(target = "typeOfWeight", ignore = true)
    @Mapping(target = "eyeCondition", ignore = true)
    @Mapping(target = "topChainCondition", ignore = true)
    @Mapping(target = "bottomChainCondition", ignore = true)
    @Mapping(target = "shackleSwivelCondition", ignore = true)
    @Mapping(target = "gpsCoordinates", ignore = true)
    @Mapping(target = "installBottomChainDate", ignore = true)
    @Mapping(target = "installTopChainDate", ignore = true)
    @Mapping(target = "installConditionOfEyeDate", ignore = true)
    @Mapping(target = "inspectionDate", ignore = true)
    Mooring mapToMooring(@MappingTarget Mooring mooring, MooringRequestDto dto);

    @Mapping(target = "customerResponseDto", ignore = true)
    @Mapping(target = "boatyardResponseDto", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "mooringDueServiceStatusDto", ignore = true)
    @Mapping(target = "mooringServiceDate", ignore = true)
    MooringDueServiceResponseDto mapToMooringDueServiceResponseDto(@MappingTarget MooringDueServiceResponseDto dto, Mooring mooring);

    @Mapping(target = "statusId", ignore = true)
    MooringWithGPSCoordinateResponse mapToMooringWithGPSCoordinateResponse(@MappingTarget MooringWithGPSCoordinateResponse mooringWithGPSCoordinateResponse, Mooring mooring);
}