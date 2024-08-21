package com.marinamooringmanagement.mapper.config;

import com.marinamooringmanagement.model.dto.config.ConfigDto;
import com.marinamooringmanagement.model.entity.config.Config;
import com.marinamooringmanagement.model.response.config.ConfigResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ConfigMapper {

    Config toEntity(@MappingTarget Config config, ConfigDto configDto);

    ConfigDto toDto(@MappingTarget ConfigDto configDto, Config config);

    ConfigResponseDto toResponseDto(@MappingTarget ConfigResponseDto configResponseDto, Config config);

}
