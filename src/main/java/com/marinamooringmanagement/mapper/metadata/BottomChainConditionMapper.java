package com.marinamooringmanagement.mapper.metadata;

import com.marinamooringmanagement.model.dto.metadata.BottomChainConditionDto;
import com.marinamooringmanagement.model.entity.metadata.BottomChainCondition;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BottomChainConditionMapper {

    BottomChainCondition mapToBottomChainCondition(@MappingTarget BottomChainCondition bottomChainCondition, BottomChainConditionDto bottomChainConditionDto);

    BottomChainConditionDto mapToBottomChainConditionDto(@MappingTarget BottomChainConditionDto bottomChainConditionDto, BottomChainCondition bottomChainCondition);

}
