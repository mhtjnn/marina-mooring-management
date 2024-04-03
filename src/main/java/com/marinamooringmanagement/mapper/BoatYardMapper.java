package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.BoatYardDto;
import com.marinamooringmanagement.model.entity.BoatYard;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper interface for converting between BoatYard and BoatYardDto objects.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

public interface BoatYardMapper {
    /**
     * Converts a BoatYard entity to a BoatYardDto.
     *
     * @param boatYard The BoatYard entity to be converted.
     * @return The corresponding BoatYardDto object.
     */
    BoatYardDto toDto(BoatYard boatYard);
    /**
     * Updates an existing BoatYard entity with data from a BoatYardDto.
     *
     * @param boatYardDto The BoatYardDto containing the updated data.
     * @param boatYard    The BoatYard entity to be updated.
     * @return The updated BoatYard entity.
     */
    BoatYard toEntity(BoatYardDto boatYardDto, @MappingTarget BoatYard boatYard);
}
