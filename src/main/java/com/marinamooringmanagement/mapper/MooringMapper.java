package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.MooringDto;
import com.marinamooringmanagement.model.entity.Mooring;
import com.marinamooringmanagement.model.request.MooringRequestDto;
import com.marinamooringmanagement.model.response.MooringResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MooringMapper {
    MooringDto mapToMooringDto(@MappingTarget MooringDto dto, Mooring mooring);

    Mooring mapToMooring(@MappingTarget Mooring mooring, MooringDto dto);

    MooringResponseDto mapToMooringResponseDto(@MappingTarget MooringResponseDto dto, Mooring mooring);

    Mooring mapToMooring(@MappingTarget Mooring mooring, MooringRequestDto dto);
}
