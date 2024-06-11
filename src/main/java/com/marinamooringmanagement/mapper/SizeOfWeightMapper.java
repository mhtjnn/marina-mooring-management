package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.SizeOfWeightDto;
import com.marinamooringmanagement.model.entity.SizeOfWeight;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SizeOfWeightMapper {

    SizeOfWeight mapToSizeOfWeight(@MappingTarget SizeOfWeight sizeOfWeight, SizeOfWeightDto sizeOfWeightDto);

    SizeOfWeightDto mapToSizeOfWeightDto(@MappingTarget SizeOfWeightDto sizeOfWeightDto, SizeOfWeight sizeOfWeight);

}
