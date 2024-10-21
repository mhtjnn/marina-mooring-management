package com.marinamooringmanagement.mapper.metadata;

import com.marinamooringmanagement.model.dto.metadata.EyeConditionDto;
import com.marinamooringmanagement.model.entity.metadata.EyeCondition;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EyeConditionMapper {

    EyeCondition mapToEyeCondition(@MappingTarget EyeCondition eyeCondition, EyeConditionDto eyeConditionDto);

    EyeConditionDto mapToEyeConditionDto(@MappingTarget EyeConditionDto eyeConditionDto, EyeCondition eyeCondition);
}
