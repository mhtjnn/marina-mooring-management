package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.ServiceAreaDto;
import com.marinamooringmanagement.model.entity.ServiceArea;
import com.marinamooringmanagement.model.request.ServiceAreaRequestDto;
import com.marinamooringmanagement.model.response.ServiceAreaResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ServiceAreaMapper {

    @Mapping(target = "serviceAreaType", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "user", ignore = true)
    ServiceArea mapToEntity(@MappingTarget ServiceArea serviceArea, ServiceAreaDto serviceAreaDto);

    @Mapping(target = "serviceAreaTypeDto", ignore = true)
    @Mapping(target = "stateDto", ignore = true)
    @Mapping(target = "countryDto", ignore = true)
    @Mapping(target = "userDto", ignore = true)
    ServiceAreaDto mapToDto(@MappingTarget ServiceAreaDto serviceAreaDto, ServiceArea serviceArea);

    @Mapping(target = "serviceAreaTypeDto", ignore = true)
    @Mapping(target = "stateResponseDto", ignore = true)
    @Mapping(target = "countryResponseDto", ignore = true)
    @Mapping(target = "userId", ignore = true)
    ServiceAreaResponseDto mapToResponseDto(@MappingTarget ServiceAreaResponseDto serviceAreaResponseDto, ServiceArea serviceArea);

    @Mapping(target = "serviceAreaType", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "user", ignore = true)
    ServiceArea mapToEntity(@MappingTarget ServiceArea serviceArea, ServiceAreaRequestDto serviceAreaRequestDto);
}
