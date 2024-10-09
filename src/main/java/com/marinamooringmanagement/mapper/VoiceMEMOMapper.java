package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.VoiceMEMODto;
import com.marinamooringmanagement.model.entity.VoiceMEMO;
import com.marinamooringmanagement.model.request.VoiceMEMORequestDto;
import com.marinamooringmanagement.model.response.VoiceMEMOResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VoiceMEMOMapper {

    @Mapping(target = "workOrder", ignore = true)
    @Mapping(target = "user", ignore = true)
    VoiceMEMO toEntity(@MappingTarget VoiceMEMO voiceMEMO, VoiceMEMODto voiceMEMODto);

    @Mapping(target = "workOrderDto", ignore = true)
    @Mapping(target = "userDto", ignore = true)
    VoiceMEMODto toDto(@MappingTarget VoiceMEMODto voiceMEMODto, VoiceMEMO voiceMEMO);

    @Mapping(target = "data", ignore = true)
    VoiceMEMO toEntity(@MappingTarget VoiceMEMO voiceMEMO, VoiceMEMORequestDto voiceMEMORequestDto);

    @Mapping(target = "encodedData", ignore = true)
    @Mapping(target = "workOrderResponseDto", ignore = true)
    @Mapping(target = "userResponseDto", ignore = true)
    VoiceMEMOResponseDto toResponseDto(@MappingTarget VoiceMEMOResponseDto voiceMEMOResponseDto, VoiceMEMO voiceMEMO);

}
