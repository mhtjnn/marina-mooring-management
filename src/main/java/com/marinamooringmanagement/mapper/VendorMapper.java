package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.VendorDto;
import com.marinamooringmanagement.model.entity.Vendor;
import com.marinamooringmanagement.model.request.VendorRequestDto;
import com.marinamooringmanagement.model.response.VendorResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VendorMapper {

    Vendor mapToVendor(@MappingTarget Vendor vendor, VendorDto dto);

    VendorDto mapToVendorDto(@MappingTarget VendorDto dto, Vendor vendor);

    VendorResponseDto mapToVendorResponseDto(@MappingTarget VendorResponseDto dto, Vendor vendor);

    Vendor mapToVendor(@MappingTarget Vendor vendor, VendorRequestDto dto);
}
