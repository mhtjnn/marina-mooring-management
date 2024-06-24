package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.ImageDto;
import com.marinamooringmanagement.model.entity.Image;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ImageMapper {

    void toDto(@MappingTarget ImageDto imageDto, Image image);

    void toEntity(@MappingTarget Image image, ImageDto imageDto);

}
