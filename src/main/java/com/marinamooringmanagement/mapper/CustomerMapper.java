package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.CustomerDto;
import com.marinamooringmanagement.model.entity.Customer;
import com.marinamooringmanagement.model.request.CustomerRequestDto;
import com.marinamooringmanagement.model.response.CustomerResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
/**
 * Mapper interface for mapping between Customer and CustomerDto objects.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomerMapper {
    /**
     * Maps a Customer entity to a CustomerDto.
     *
     * @param customer The Customer entity to map.
     * @return The mapped CustomerDto object.
     */
    CustomerDto toDto(Customer customer);

    /**
     * Maps a CustomerDto to a Customer entity.
     *
     * @param customerDto The CustomerDto to map.
     * @param customer    The target Customer entity.
     * @return The mapped Customer entity.
     */
    Customer toEntity(CustomerDto customerDto, @MappingTarget Customer customer);

    CustomerResponseDto mapToCustomerResponseDto(@MappingTarget CustomerResponseDto dto, Customer customer);

    Customer mapToCustomer(@MappingTarget Customer customer, CustomerRequestDto dto);
}
