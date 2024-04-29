package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.MooringDto;
import com.marinamooringmanagement.model.entity.Mooring;
import com.marinamooringmanagement.model.request.MooringRequestDto;
import com.marinamooringmanagement.model.response.MooringResponseDto;
import org.mapstruct.Mapper;
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
    MooringDto mapToMooringDto(@MappingTarget MooringDto dto, Mooring mooring);

    /**
     * Maps a MooringDto to a Mooring entity.
     *
     * @param mooring The target Mooring entity to map to.
     * @param dto     The source MooringDto object to map from.
     * @return The mapped Mooring entity.
     */
    Mooring mapToMooring(@MappingTarget Mooring mooring, MooringDto dto);

    /**
     * Maps a Mooring entity to a MooringResponseDto.
     *
     * @param dto     The target MooringResponseDto object to map to.
     * @param mooring The source Mooring entity to map from.
     * @return The mapped MooringResponseDto object.
     */
    MooringResponseDto mapToMooringResponseDto(@MappingTarget MooringResponseDto dto, Mooring mooring);

    /**
     * Maps a MooringRequestDto to a Mooring entity.
     *
     * @param mooring The target Mooring entity to map to.
     * @param dto     The source MooringRequestDto object to map from.
     * @return The mapped Mooring entity.
     */
    Mooring mapToMooring(@MappingTarget Mooring mooring, MooringRequestDto dto);
}