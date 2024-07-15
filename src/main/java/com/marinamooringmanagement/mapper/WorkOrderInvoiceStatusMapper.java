package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.WorkOrderInvoiceStatusDto;
import com.marinamooringmanagement.model.entity.WorkOrderInvoiceStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkOrderInvoiceStatusMapper {
    WorkOrderInvoiceStatusDto toDto(@MappingTarget WorkOrderInvoiceStatusDto workOrderInvoiceStatusDto, WorkOrderInvoiceStatus workOrderInvoiceStatus);

    WorkOrderInvoiceStatus toEntity(@MappingTarget WorkOrderInvoiceStatus workOrderInvoiceStatus, WorkOrderInvoiceStatusDto workOrderInvoiceStatusDto);
}
