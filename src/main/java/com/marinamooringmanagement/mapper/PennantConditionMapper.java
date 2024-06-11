package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.PennantConditionDto;
import com.marinamooringmanagement.model.entity.PennantCondition;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PennantConditionMapper {

    PennantCondition mapToPennantCondition(@MappingTarget PennantCondition pennantCondition, PennantConditionDto pennantConditionDto);

    PennantConditionDto mapToPennantConditionDto(@MappingTarget PennantConditionDto pennantConditionDto, PennantCondition pennantCondition);

}
