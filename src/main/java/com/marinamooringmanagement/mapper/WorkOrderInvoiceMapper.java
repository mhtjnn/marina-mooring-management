package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.WorkOrderInvoiceDto;
import com.marinamooringmanagement.model.entity.WorkOrderInvoice;
import com.marinamooringmanagement.model.response.WorkOrderInvoiceResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkOrderInvoiceMapper {

    @Mapping(target = "workOrder", ignore = true)
    @Mapping(target = "workOrderInvoiceStatus", ignore = true)
    WorkOrderInvoice mapToWorkOrderInvoice(@MappingTarget WorkOrderInvoice workOrderInvoice, WorkOrderInvoiceDto workOrderInvoiceDto);

    @Mapping(target = "workOrderDto", ignore = true)
    @Mapping(target = "workOrderInvoiceStatusDto", ignore = true)
    WorkOrderInvoiceDto mapToWorkOrderInvoiceDto(@MappingTarget WorkOrderInvoiceDto workOrderInvoiceDto, WorkOrderInvoice workOrder);

    @Mapping(target = "workOrderResponseDto", ignore = true)
    @Mapping(target = "workOrderInvoiceStatusDto", ignore = true)
    WorkOrderInvoiceResponseDto mapToWorkOrderInvoiceResponseDto(@MappingTarget WorkOrderInvoiceResponseDto workOrderResponseDto, WorkOrderInvoice workOrder);

}
