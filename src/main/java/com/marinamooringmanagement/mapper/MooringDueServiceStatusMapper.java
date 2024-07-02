package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.MooringDueServiceStatusDto;
import com.marinamooringmanagement.model.entity.MooringDueServiceStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MooringDueServiceStatusMapper {

    void toEntity(@MappingTarget MooringDueServiceStatus entity, MooringDueServiceStatusDto dto);

    void toDto(@MappingTarget MooringDueServiceStatusDto dto, MooringDueServiceStatus entity);

}
