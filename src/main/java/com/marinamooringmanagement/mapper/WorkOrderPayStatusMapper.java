package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.WorkOrderPayStatusDto;
import com.marinamooringmanagement.model.entity.WorkOrderPayStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkOrderPayStatusMapper {

    WorkOrderPayStatusDto toDto(@MappingTarget WorkOrderPayStatusDto workOrderPayStatusDto, WorkOrderPayStatus workOrderPayStatus);

    WorkOrderPayStatus toEntity(@MappingTarget WorkOrderPayStatus workOrderPayStatus, WorkOrderPayStatusDto workOrderPayStatusDto);

}
