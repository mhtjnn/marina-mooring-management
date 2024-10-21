package com.marinamooringmanagement.mapper.metadata;

import com.marinamooringmanagement.model.dto.metadata.PaymentTypeDto;
import com.marinamooringmanagement.model.entity.metadata.PaymentType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentTypeMapper {

    PaymentType mapToEntity(@MappingTarget PaymentType paymentType, PaymentTypeDto paymentTypeDto);
    PaymentTypeDto mapToDto(@MappingTarget PaymentTypeDto paymentTypeDto, PaymentType paymentType);

}
