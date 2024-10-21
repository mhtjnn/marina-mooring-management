package com.marinamooringmanagement.mapper.metadata;

import com.marinamooringmanagement.model.dto.metadata.PennantConditionDto;
import com.marinamooringmanagement.model.entity.metadata.PennantCondition;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PennantConditionMapper {

    PennantCondition mapToPennantCondition(@MappingTarget PennantCondition pennantCondition, PennantConditionDto pennantConditionDto);

    PennantConditionDto mapToPennantConditionDto(@MappingTarget PennantConditionDto pennantConditionDto, PennantCondition pennantCondition);

}
