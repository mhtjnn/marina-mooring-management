package com.marinamooringmanagement.mapper.metadata;

import com.marinamooringmanagement.model.dto.metadata.ShackleSwivelConditionDto;
import com.marinamooringmanagement.model.entity.metadata.ShackleSwivelCondition;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ShackleSwivelConditionMapper {

    ShackleSwivelCondition mapToShackleSwivelCondition(@MappingTarget ShackleSwivelCondition shackleSwivelCondition, ShackleSwivelConditionDto shackleSwivelConditionDto);

    ShackleSwivelConditionDto mapToShackleSwivelConditionDto(@MappingTarget ShackleSwivelConditionDto shackleSwivelConditionDto, ShackleSwivelCondition shackleSwivelCondition);

}
