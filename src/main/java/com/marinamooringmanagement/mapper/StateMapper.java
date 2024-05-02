package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.StateDto;
import com.marinamooringmanagement.model.entity.State;
import com.marinamooringmanagement.model.request.StateRequestDto;
import com.marinamooringmanagement.model.response.StateResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StateMapper {

    StateDto mapToStateDto(@MappingTarget StateDto dto, State state);

    void mapToState(@MappingTarget State entity, StateDto stateDto);

    void mapToState(@MappingTarget State entity, StateRequestDto stateRequestDto);

    StateResponseDto mapToStateResponseDto(@MappingTarget StateResponseDto dto, State state);
}
