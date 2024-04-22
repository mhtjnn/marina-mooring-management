package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.WorkOrderDto;
import com.marinamooringmanagement.model.entity.WorkOrder;
import com.marinamooringmanagement.model.request.WorkOrderRequestDto;
import com.marinamooringmanagement.model.response.WorkOrderResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper interface for mapping between WorkOrder entities, DTOs, and response DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkOrderMapper {

    /**
     * Maps a WorkOrder entity to a WorkOrderDto.
     *
     * @param workOrder The WorkOrder entity to map.
     * @return WorkOrderDto object.
     */
    WorkOrderDto toDto(WorkOrder workOrder);

    /**
     * Maps a WorkOrderDto to a WorkOrder entity.
     *
     * @param workOrderDto The WorkOrderDto to map.
     * @param workOrder    The target WorkOrder entity to map to.
     * @return The mapped WorkOrder entity.
     */
    WorkOrder toEntity(WorkOrderDto workOrderDto, @MappingTarget WorkOrder workOrder);
    /**
     * Maps a WorkOrder entity to a WorkOrderResponseDto.
     *
     * @param dto       The target WorkOrderResponseDto to map to.
     * @param workOrder The WorkOrder entity to map.
     * @return The mapped WorkOrderResponseDto.
     */
    WorkOrderResponseDto mapToWorkResponseDto(@MappingTarget WorkOrderResponseDto dto, WorkOrder workOrder);

    /**
     * Maps a WorkOrderRequestDto to a WorkOrder entity.
     *
     * @param workOrder    The target WorkOrder entity to map to.
     * @param dto          The WorkOrderRequestDto to map.
     * @return The mapped WorkOrder entity.
     */
    WorkOrder mapToWorkOrder(@MappingTarget WorkOrder workOrder, WorkOrderRequestDto dto);
}