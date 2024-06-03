package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.TopChainConditionDto;
import com.marinamooringmanagement.model.entity.TopChainCondition;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TopChainConditionMapper {

    TopChainCondition mapToTopChainCondition(@MappingTarget TopChainCondition topChainCondition, TopChainConditionDto topChainConditionDto);

    TopChainConditionDto mapToTopChainConditionDto(@MappingTarget TopChainConditionDto topChainConditionDto, TopChainCondition topChainCondition);

}
