package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.WorkOrderDto;
import com.marinamooringmanagement.model.entity.WorkOrder;
import com.marinamooringmanagement.model.request.WorkOrderRequestDto;
import com.marinamooringmanagement.model.response.WorkOrderResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkOrderMapper {

    @Mapping(target = "mooring", ignore = true)
    @Mapping(target = "technicianUser", ignore = true)
    @Mapping(target = "workOrderStatus", ignore = true)
    @Mapping(target = "workOrderPayStatus", ignore = true)
    @Mapping(target = "customerOwnerUser", ignore = true)
    @Mapping(target = "imageList", ignore = true)
    WorkOrder mapToWorkOrder(@MappingTarget WorkOrder workOrder, WorkOrderDto workOrderDto);

    @Mapping(target = "mooring", ignore = true)
    @Mapping(target = "technicianUser", ignore = true)
    @Mapping(target = "workOrderStatus", ignore = true)
    @Mapping(target = "workOrderPayStatus", ignore = true)
    @Mapping(target = "customerOwnerUser", ignore = true)
    @Mapping(target = "dueDate", ignore = true)
    @Mapping(target = "scheduledDate", ignore = true)
    @Mapping(target = "imageList", ignore = true)
    @Mapping(target = "completedDate", ignore = true)
    void mapToWorkOrder(@MappingTarget WorkOrder workOrder, WorkOrderRequestDto workOrderRequestDto);

    @Mapping(target = "mooringDto", ignore = true)
    @Mapping(target = "customerOwnerUserDto", ignore = true)
    @Mapping(target = "workOrderStatusDto", ignore = true)
    @Mapping(target = "workOrderPayStatusDto", ignore = true)
    @Mapping(target = "technicianUserDto", ignore = true)
    @Mapping(target = "imageDtoList", ignore = true)
    WorkOrderDto mapToWorkOrderDto(@MappingTarget WorkOrderDto workOrderDto, WorkOrder workOrder);

    @Mapping(target = "customerResponseDto", ignore = true)
    @Mapping(target = "mooringResponseDto", ignore = true)
    @Mapping(target = "boatyardResponseDto", ignore = true)
    @Mapping(target = "technicianUserResponseDto", ignore = true)
    @Mapping(target = "customerOwnerUserResponseDto", ignore = true)
    @Mapping(target = "workOrderStatusDto", ignore = true)
    @Mapping(target = "workOrderPayStatusDto", ignore = true)
    @Mapping(target = "dueDate", ignore = true)
    @Mapping(target = "scheduledDate", ignore = true)
    @Mapping(target = "completedDate", ignore = true)
    WorkOrderResponseDto mapToWorkOrderResponseDto(@MappingTarget WorkOrderResponseDto workOrderResponseDto, WorkOrder workOrder);

}
