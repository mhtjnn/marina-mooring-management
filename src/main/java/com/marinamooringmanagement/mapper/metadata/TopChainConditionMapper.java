package com.marinamooringmanagement.mapper.metadata;

import com.marinamooringmanagement.model.dto.metadata.TopChainConditionDto;
import com.marinamooringmanagement.model.entity.metadata.TopChainCondition;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TopChainConditionMapper {

    TopChainCondition mapToTopChainCondition(@MappingTarget TopChainCondition topChainCondition, TopChainConditionDto topChainConditionDto);

    TopChainConditionDto mapToTopChainConditionDto(@MappingTarget TopChainConditionDto topChainConditionDto, TopChainCondition topChainCondition);

}
