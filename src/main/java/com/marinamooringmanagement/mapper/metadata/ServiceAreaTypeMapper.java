package com.marinamooringmanagement.mapper.metadata;

import com.marinamooringmanagement.model.dto.metadata.ServiceAreaTypeDto;
import com.marinamooringmanagement.model.entity.metadata.ServiceAreaType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ServiceAreaTypeMapper {

    ServiceAreaType toEntity(@MappingTarget ServiceAreaType serviceAreaType, ServiceAreaTypeDto serviceAreaTypeDto);

    ServiceAreaTypeDto toDto(@MappingTarget ServiceAreaTypeDto serviceAreaTypeDto, ServiceAreaType serviceAreaType);

}
