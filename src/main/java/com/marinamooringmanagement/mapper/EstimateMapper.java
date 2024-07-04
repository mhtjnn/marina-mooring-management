package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.EstimateDto;
import com.marinamooringmanagement.model.entity.Estimate;
import com.marinamooringmanagement.model.entity.WorkOrder;
import com.marinamooringmanagement.model.request.EstimateRequestDto;
import com.marinamooringmanagement.model.response.EstimateResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EstimateMapper {

    @Mapping(target = "mooring", ignore = true)
    @Mapping(target = "technicianUser", ignore = true)
    @Mapping(target = "workOrderStatus", ignore = true)
    @Mapping(target = "customerOwnerUser", ignore = true)
    Estimate mapToEstimate(@MappingTarget Estimate estimate, EstimateDto estimateDto);

    @Mapping(target = "mooring", ignore = true)
    @Mapping(target = "technicianUser", ignore = true)
    @Mapping(target = "workOrderStatus", ignore = true)
    @Mapping(target = "customerOwnerUser", ignore = true)
    @Mapping(target = "dueDate", ignore = true)
    @Mapping(target = "scheduledDate", ignore = true)
    Estimate mapToEstimate(@MappingTarget Estimate estimate, EstimateRequestDto estimateRequestDto);

    @Mapping(target = "mooringDto", ignore = true)
    @Mapping(target = "customerOwnerUserDto", ignore = true)
    @Mapping(target = "workOrderStatusDto", ignore = true)
    @Mapping(target = "technicianUserDto", ignore = true)
    EstimateDto mapToEstimateDto(@MappingTarget EstimateDto estimateDto, Estimate estimate);

    @Mapping(target = "customerResponseDto", ignore = true)
    @Mapping(target = "mooringResponseDto", ignore = true)
    @Mapping(target = "boatyardResponseDto", ignore = true)
    @Mapping(target = "technicianUserResponseDto", ignore = true)
    @Mapping(target = "customerOwnerUserResponseDto", ignore = true)
    @Mapping(target = "workOrderStatusDto", ignore = true)
    @Mapping(target = "dueDate", ignore = true)
    @Mapping(target = "scheduledDate", ignore = true)
    EstimateResponseDto mapToEstimateResponseDto(@MappingTarget EstimateResponseDto estimateResponseDto, Estimate estimate);

    @Mapping(target = "mooring", ignore = true)
    @Mapping(target = "technicianUser", ignore = true)
    @Mapping(target = "workOrderStatus", ignore = true)
    @Mapping(target = "customerOwnerUser", ignore = true)
    WorkOrder mapToWorkOrder(@MappingTarget WorkOrder workOrder, Estimate estimate);
}
