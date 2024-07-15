package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.ImageDto;
import com.marinamooringmanagement.model.entity.Image;
import com.marinamooringmanagement.model.request.ImageRequestDto;
import com.marinamooringmanagement.model.response.ImageResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ImageMapper {

    ImageDto toDto(@MappingTarget ImageDto imageDto, Image image);

    void toEntity(@MappingTarget Image image, ImageDto imageDto);

    @Mapping(target = "imageData", ignore = true)
    Image toEntity(@MappingTarget Image image, ImageRequestDto imageDto);

    ImageResponseDto toResponseDto(@MappingTarget ImageResponseDto imageResponseDto, Image image);
}
