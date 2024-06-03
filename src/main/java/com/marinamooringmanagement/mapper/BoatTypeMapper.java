package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.BoatTypeDto;
import com.marinamooringmanagement.model.entity.BoatType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BoatTypeMapper {

    BoatTypeDto mapToBoatTypeDto(@MappingTarget BoatTypeDto boatTypeDto, BoatType boatType);

    BoatType mapToBoatType(@MappingTarget BoatType boatType, BoatTypeDto boatTypeDto);

}
