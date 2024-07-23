package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.PaymentDto;
import com.marinamooringmanagement.model.entity.Payment;
import com.marinamooringmanagement.model.request.PaymentRequestDto;
import com.marinamooringmanagement.model.response.PaymentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMapper {

    @Mapping(target = "paymentTypeDto", ignore = true)
    @Mapping(target = "customerOwnerUserDto", ignore = true)
    @Mapping(target = "workOrderInvoiceDto", ignore = true)
    PaymentDto mapToDto(@MappingTarget PaymentDto paymentDto, Payment payment);

    @Mapping(target = "paymentType", ignore = true)
    @Mapping(target = "customerOwnerUser", ignore = true)
    @Mapping(target = "workOrderInvoice", ignore = true)
    Payment mapToEntity(@MappingTarget Payment payment, PaymentDto paymentDto);

    @Mapping(target = "paymentType", ignore = true)
    @Mapping(target = "customerOwnerUser", ignore = true)
    @Mapping(target = "workOrderInvoice", ignore = true)
    Payment mapToEntity(@MappingTarget Payment payment, PaymentRequestDto paymentRequestDto);

    @Mapping(target = "paymentTypeDto", ignore = true)
    @Mapping(target = "customerOwnerUserResponseDto", ignore = true)
    @Mapping(target = "workOrderInvoiceResponseDto", ignore = true)
    PaymentResponseDto mapToResponseDto(@MappingTarget PaymentResponseDto paymentResponseDto, Payment payment);
}
