package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.ShackleSwivelConditionDto;
import com.marinamooringmanagement.model.entity.ShackleSwivelCondition;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ShackleSwivelConditionMapper {

    ShackleSwivelCondition mapToShackleSwivelCondition(@MappingTarget ShackleSwivelCondition shackleSwivelCondition, ShackleSwivelConditionDto shackleSwivelConditionDto);

    ShackleSwivelConditionDto mapToShackleSwivelConditionDto(@MappingTarget ShackleSwivelConditionDto shackleSwivelConditionDto, ShackleSwivelCondition shackleSwivelCondition);

}
