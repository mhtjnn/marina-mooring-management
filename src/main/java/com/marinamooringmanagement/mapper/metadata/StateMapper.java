package com.marinamooringmanagement.mapper.metadata;

import com.marinamooringmanagement.model.dto.metadata.StateDto;
import com.marinamooringmanagement.model.entity.metadata.State;
import com.marinamooringmanagement.model.request.StateRequestDto;
import com.marinamooringmanagement.model.response.metadata.StateResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper interface for mapping between {@link State} entity and its corresponding DTOs.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StateMapper {

    /**
     * Maps a {@link State} entity to a {@link StateDto}.
     *
     * @param dto   The target {@link StateDto} to map to.
     * @param state The {@link State} entity to map from.
     * @return The mapped {@link StateDto}.
     */
    @Mapping(target = "countryDto", ignore = true)
    StateDto mapToStateDto(@MappingTarget StateDto dto, State state);

    /**
     * Maps data from a {@link StateDto} to a {@link State} entity.
     *
     * @param entity    The target {@link State} entity to map to.
     * @param stateDto  The {@link StateDto} containing data to map from.
     */
    @Mapping(target = "country", ignore = true)
    void mapToState(@MappingTarget State entity, StateDto stateDto);

    /**
     * Maps data from a {@link StateRequestDto} to a {@link State} entity.
     *
     * @param entity            The target {@link State} entity to map to.
     * @param stateRequestDto   The {@link StateRequestDto} containing data to map from.
     */
    @Mapping(target = "country", ignore = true)
    void mapToState(@MappingTarget State entity, StateRequestDto stateRequestDto);

    /**
     * Maps a {@link State} entity to a {@link StateResponseDto}.
     *
     * @param dto   The target {@link StateResponseDto} to map to.
     * @param state The {@link State} entity to map from.
     * @return The mapped {@link StateResponseDto}.
     */
    @Mapping(target = "countryResponseDto", ignore = true)
    StateResponseDto mapToStateResponseDto(@MappingTarget StateResponseDto dto, State state);
}
