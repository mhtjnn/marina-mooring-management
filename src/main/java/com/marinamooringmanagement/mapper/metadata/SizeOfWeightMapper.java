package com.marinamooringmanagement.mapper.metadata;

import com.marinamooringmanagement.model.dto.metadata.SizeOfWeightDto;
import com.marinamooringmanagement.model.entity.metadata.SizeOfWeight;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SizeOfWeightMapper {

    SizeOfWeight mapToSizeOfWeight(@MappingTarget SizeOfWeight sizeOfWeight, SizeOfWeightDto sizeOfWeightDto);

    SizeOfWeightDto mapToSizeOfWeightDto(@MappingTarget SizeOfWeightDto sizeOfWeightDto, SizeOfWeight sizeOfWeight);

}
