package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.FormDto;
import com.marinamooringmanagement.model.entity.Form;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * The FormMapper interface provides methods for mapping between {@link Form} and {@link FormDto}.
 *
 * It uses MapStruct for automatic mapping between the two types, with a component model of "spring" for dependency injection.
 * Mapping strategy is set to ignore null values to avoid overwriting existing values with null values.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FormMapper {

    /**
     * Maps a {@link Form} entity to a {@link FormDto}.
     *
     * The method takes an existing {@link FormDto} and updates it with the values from the provided {@link Form} entity.
     *
     * @param formDto The target {@link FormDto} to be updated.
     * @param form    The source {@link Form} entity providing data for the mapping.
     * @return The updated {@link FormDto} with data from the provided {@link Form}.
     */
    FormDto mapToFormDto(@MappingTarget FormDto formDto, Form form);

    /**
     * Maps a {@link FormDto} to a {@link Form} entity.
     *
     * The method takes an existing {@link Form} entity and updates it with the values from the provided {@link FormDto}.
     *
     * @param form    The target {@link Form} entity to be updated.
     * @param formDto The source {@link FormDto} providing data for the mapping.
     * @return The updated {@link Form} entity with data from the provided {@link FormDto}.
     */
    Form mapToForm(@MappingTarget Form form, FormDto formDto);
}
