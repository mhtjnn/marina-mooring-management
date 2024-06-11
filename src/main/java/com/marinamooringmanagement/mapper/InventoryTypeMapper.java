package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.InventoryTypeDto;

import com.marinamooringmanagement.model.entity.InventoryType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InventoryTypeMapper {

    InventoryType mapToInventoryType(@MappingTarget InventoryType inventoryType, InventoryTypeDto inventoryTypeDto);

    InventoryTypeDto mapToInventoryTypeDto(@MappingTarget InventoryTypeDto inventoryTypeDto, InventoryType inventoryType);

}
