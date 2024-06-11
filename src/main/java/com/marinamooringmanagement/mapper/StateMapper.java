package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.StateDto;
import com.marinamooringmanagement.model.entity.State;
import com.marinamooringmanagement.model.request.StateRequestDto;
import com.marinamooringmanagement.model.response.StateResponseDto;
import org.mapstruct.Mapper;
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
    StateDto mapToStateDto(@MappingTarget StateDto dto, State state);

    /**
     * Maps data from a {@link StateDto} to a {@link State} entity.
     *
     * @param entity    The target {@link State} entity to map to.
     * @param stateDto  The {@link StateDto} containing data to map from.
     */
    void mapToState(@MappingTarget State entity, StateDto stateDto);

    /**
     * Maps data from a {@link StateRequestDto} to a {@link State} entity.
     *
     * @param entity            The target {@link State} entity to map to.
     * @param stateRequestDto   The {@link StateRequestDto} containing data to map from.
     */
    void mapToState(@MappingTarget State entity, StateRequestDto stateRequestDto);

    /**
     * Maps a {@link State} entity to a {@link StateResponseDto}.
     *
     * @param dto   The target {@link StateResponseDto} to map to.
     * @param state The {@link State} entity to map from.
     * @return The mapped {@link StateResponseDto}.
     */
    StateResponseDto mapToStateResponseDto(@MappingTarget StateResponseDto dto, State state);
}
