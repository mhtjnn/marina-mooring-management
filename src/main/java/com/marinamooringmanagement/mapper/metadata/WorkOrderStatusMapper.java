package com.marinamooringmanagement.mapper.metadata;

import com.marinamooringmanagement.model.dto.metadata.WorkOrderStatusDto;
import com.marinamooringmanagement.model.entity.metadata.WorkOrderStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkOrderStatusMapper {

    WorkOrderStatus mapToEntity(@MappingTarget WorkOrderStatus workOrderStatus, WorkOrderStatusDto workOrderStatusDto);

    WorkOrderStatusDto mapToDto(@MappingTarget WorkOrderStatusDto workOrderStatusDto, WorkOrderStatus workOrderStatus);

}
