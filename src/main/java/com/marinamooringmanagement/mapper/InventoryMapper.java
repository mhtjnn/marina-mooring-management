package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.InventoryDto;
import com.marinamooringmanagement.model.entity.Inventory;
import com.marinamooringmanagement.model.request.InventoryRequestDto;
import com.marinamooringmanagement.model.response.InventoryResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InventoryMapper {

    @Mapping(target = "vendor", ignore = true)
    @Mapping(target = "workOrder", ignore = true)
    Inventory mapToInventory(@MappingTarget Inventory inventory, InventoryDto inventoryDto);

    @Mapping(target = "vendorDto", ignore = true)
    @Mapping(target = "workOrderDto", ignore = true)
    InventoryDto mapToInventoryDto(@MappingTarget InventoryDto inventoryDto, Inventory inventory);

    @Mapping(target = "vendor", ignore = true)
    @Mapping(target = "taxable", ignore = true)
    @Mapping(target = "workOrder", ignore = true)
    Inventory mapToInventory(@MappingTarget Inventory inventory, InventoryRequestDto inventoryRequestDto);

    @Mapping(target = "taxable", ignore = true)
    @Mapping(target = "vendorResponseDto", ignore = true)
    @Mapping(target = "workOrderResponseDto", ignore = true)
    InventoryResponseDto mapToInventoryResponseDto(@MappingTarget InventoryResponseDto inventoryResponseDto, Inventory inventory);

    @Mapping(target = "id", ignore = true)
    Inventory mapToInventory(@MappingTarget Inventory childInventory, Inventory parentInventory);

}
