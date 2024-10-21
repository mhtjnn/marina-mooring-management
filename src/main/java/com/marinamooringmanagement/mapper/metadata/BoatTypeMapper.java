package com.marinamooringmanagement.mapper.metadata;

import com.marinamooringmanagement.model.dto.metadata.BoatTypeDto;
import com.marinamooringmanagement.model.entity.metadata.BoatType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BoatTypeMapper {

    BoatTypeDto mapToBoatTypeDto(@MappingTarget BoatTypeDto boatTypeDto, BoatType boatType);

    BoatType mapToBoatType(@MappingTarget BoatType boatType, BoatTypeDto boatTypeDto);

}
