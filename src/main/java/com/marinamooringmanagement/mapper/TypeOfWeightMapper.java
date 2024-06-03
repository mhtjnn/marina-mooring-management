package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.TypeOfWeightDto;
import com.marinamooringmanagement.model.entity.TypeOfWeight;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TypeOfWeightMapper {

    TypeOfWeight mapToTypeOfWeight(@MappingTarget TypeOfWeight typeOfWeight, TypeOfWeightDto typeOfWeightDto);

    TypeOfWeightDto mapToTypeOfWeightDto(@MappingTarget TypeOfWeightDto typeOfWeightDto, TypeOfWeight typeOfWeight);

}
