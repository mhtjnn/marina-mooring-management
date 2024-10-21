package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.CustomerDto;
import com.marinamooringmanagement.model.entity.Customer;
import com.marinamooringmanagement.model.request.CustomerRequestDto;
import com.marinamooringmanagement.model.response.CustomerResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
    @Mapping(target = "mooringDtoList", ignore = true)
    @Mapping(target = "userDto", ignore = true)
    @Mapping(target = "customerTypeDto", ignore = true)
    @Mapping(target = "imageDtoList", ignore = true)
    CustomerDto toDto(@MappingTarget CustomerDto dto, Customer customer);

    /**
     * Maps a CustomerDto to a Customer entity.
     *
     * @param customerDto The CustomerDto to map.
     * @param customer    The target Customer entity.
     * @return The mapped Customer entity.
     */
    @Mapping(target = "mooringList", ignore = true)
    @Mapping(target = "customerType", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "imageList", ignore = true)
    Customer toEntity(@MappingTarget Customer customer, CustomerDto customerDto);

    @Mapping(target = "mooringResponseDtoList", ignore = true)
    @Mapping(target = "stateResponseDto", ignore = true)
    @Mapping(target = "countryResponseDto", ignore = true)
    @Mapping(target = "customerTypeDto", ignore = true)
    @Mapping(target = "imageDtoList", ignore = true)
    @Mapping(target = "quickbookCustomerResponseDto", ignore = true)
    CustomerResponseDto mapToCustomerResponseDto(@MappingTarget CustomerResponseDto dto, Customer customer);

    @Mapping(target = "mooringList", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "customerType", ignore = true)
    @Mapping(target = "imageList", ignore = true)
    Customer mapToCustomer(@MappingTarget Customer customer, CustomerRequestDto dto);
}
