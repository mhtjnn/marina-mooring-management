package com.marinamooringmanagement.mapper.metadata;

import com.marinamooringmanagement.model.dto.metadata.TypeOfWeightDto;
import com.marinamooringmanagement.model.entity.metadata.TypeOfWeight;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TypeOfWeightMapper {

    TypeOfWeight mapToTypeOfWeight(@MappingTarget TypeOfWeight typeOfWeight, TypeOfWeightDto typeOfWeightDto);

    TypeOfWeightDto mapToTypeOfWeightDto(@MappingTarget TypeOfWeightDto typeOfWeightDto, TypeOfWeight typeOfWeight);

}
