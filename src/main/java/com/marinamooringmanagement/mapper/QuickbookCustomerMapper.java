package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.QuickbookCustomerDto;
import com.marinamooringmanagement.model.entity.QuickbookCustomer;
import com.marinamooringmanagement.model.request.QuickbookCustomerRequestDto;
import com.marinamooringmanagement.model.response.QuickbookCustomerResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface QuickbookCustomerMapper {

    @Mapping(target = "user", ignore = true)
    QuickbookCustomer mapToEntity(@MappingTarget QuickbookCustomer entity, QuickbookCustomerDto dto);

    @Mapping(target = "userDto", ignore = true)
    QuickbookCustomerDto mapToDto(@MappingTarget QuickbookCustomerDto dto, QuickbookCustomer entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "quickbookCustomerId", ignore = true)
    QuickbookCustomer mapToEntity(@MappingTarget QuickbookCustomer entity, QuickbookCustomerRequestDto dto);

    @Mapping(target = "userId", ignore = true)
    QuickbookCustomerResponseDto mapToResponseDto(@MappingTarget QuickbookCustomerResponseDto dto, QuickbookCustomer entity);

}
