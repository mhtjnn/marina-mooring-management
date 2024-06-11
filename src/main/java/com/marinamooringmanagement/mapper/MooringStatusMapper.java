package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.MooringStatusDto;
import com.marinamooringmanagement.model.entity.MooringStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MooringStatusMapper {

    MooringStatusDto mapToMooringStatusDto(@MappingTarget MooringStatusDto dto, MooringStatus entity);
}
