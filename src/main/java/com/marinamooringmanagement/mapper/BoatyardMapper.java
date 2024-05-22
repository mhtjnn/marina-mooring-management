package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.BoatyardDto;
import com.marinamooringmanagement.model.entity.Boatyard;
import com.marinamooringmanagement.model.request.BoatyardRequestDto;
import com.marinamooringmanagement.model.response.BoatyardResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper interface for converting between BoatYard and BoatYardDto objects.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BoatyardMapper {

    /**
     * Converts a BoatYard entity to a BoatYardDto.
     *
     * @param boatYard The BoatYard entity to be converted.
     * @return The corresponding BoatYardDto object.
     */
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "mooringDtoList", ignore = true)
    BoatyardDto toDto(Boatyard boatYard);

    /**
     * Updates an existing BoatYard entity with data from a BoatYardDto.
     *
     * @param boatYardDto The BoatYardDto containing the updated data.
     * @param boatYard    The BoatYard entity to be updated.
     * @return The updated BoatYard entity.
     */
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "mooringList", ignore = true)
    Boatyard toEntity(BoatyardDto boatYardDto, @MappingTarget Boatyard boatYard);

    /**
     * Maps a BoatYard entity to a BoatYardResponseDto.
     *
     * @param dto      The BoatYardResponseDto object to be mapped.
     * @param boatYard The BoatYard entity to be mapped.
     * @return The mapped BoatYardResponseDto.
     */
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "country", ignore = true)
    BoatyardResponseDto mapToBoatYardResponseDto(@MappingTarget BoatyardResponseDto dto, Boatyard boatYard);

    /**
     * Maps a BoatYardRequestDto to a BoatYard entity.
     *
     * @param boatYard    The BoatYard entity to be mapped.
     * @param boatYardDto The BoatYardRequestDto containing the data to be mapped.
     * @return The mapped BoatYard entity.
     */
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "mooringList", ignore = true)
    Boatyard mapToBoatYard(@MappingTarget Boatyard boatYard, BoatyardRequestDto boatYardDto);
}
