package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.FormDto;
import com.marinamooringmanagement.model.dto.ImageDto;
import com.marinamooringmanagement.model.entity.Form;
import com.marinamooringmanagement.model.request.FormRequestDto;
import com.marinamooringmanagement.model.response.FormResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FormMapper {

    @Mapping(target = "userDto", ignore = true)
    FormDto toDto(@MappingTarget FormDto formDto, Form form);

    @Mapping(target = "user", ignore = true)
    void toEntity(@MappingTarget Form form, ImageDto imageDto);

    @Mapping(target = "formData", ignore = true)
    @Mapping(target = "user", ignore = true)
    Form toEntity(@MappingTarget Form form, FormRequestDto imageDto);

    @Mapping(target = "userResponseDto", ignore = true)
    @Mapping(target = "submittedBy", ignore = true)
    @Mapping(target = "submittedDate", ignore = true)
    FormResponseDto toResponseDto(@MappingTarget FormResponseDto formResponseDto, Form form);
}
