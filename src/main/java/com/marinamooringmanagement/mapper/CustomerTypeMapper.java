package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.CustomerTypeDto;
import com.marinamooringmanagement.model.entity.CustomerType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomerTypeMapper {

    CustomerType toEntity(@MappingTarget CustomerType customerType, CustomerTypeDto customerTypeDto);

    CustomerTypeDto toDto(@MappingTarget CustomerTypeDto customerTypeDto, CustomerType customerType);

}
