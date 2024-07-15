package com.marinamooringmanagement.mapper.metadata;

import com.marinamooringmanagement.model.dto.metadata.MooringStatusDto;
import com.marinamooringmanagement.model.entity.metadata.MooringStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MooringStatusMapper {

    MooringStatusDto mapToMooringStatusDto(@MappingTarget MooringStatusDto dto, MooringStatus entity);
}
