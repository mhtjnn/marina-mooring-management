package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.TechnicianDto;
import com.marinamooringmanagement.model.entity.Technician;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper interface for mapping between Technician and TechnicianDto objects.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

public interface TechnicianMapper {

    /**
     * Maps a Technician entity to a TechnicianDto.
     *
     * @param technician The Technician entity to map.
     * @return The mapped TechnicianDto object.
     */
    TechnicianDto toDto(Technician technician);


    /**
     * Maps a TechnicianDto to a Technician entity.
     *
     * @param technicianDto The TechnicianDto to map.
     * @param technician    The target Technician entity.
     * @return The mapped Technician entity.
     */
    Technician toEntity(TechnicianDto technicianDto, @MappingTarget Technician technician);


}
