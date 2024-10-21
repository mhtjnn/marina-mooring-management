package com.marinamooringmanagement.mapper.metadata;

import com.marinamooringmanagement.model.dto.metadata.MooringDueServiceStatusDto;
import com.marinamooringmanagement.model.entity.metadata.MooringDueServiceStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MooringDueServiceStatusMapper {

    void toEntity(@MappingTarget MooringDueServiceStatus entity, MooringDueServiceStatusDto dto);

    void toDto(@MappingTarget MooringDueServiceStatusDto dto, MooringDueServiceStatus entity);

}
