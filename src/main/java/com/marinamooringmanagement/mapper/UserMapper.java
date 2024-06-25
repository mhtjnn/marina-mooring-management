package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.request.UserRequestDto;
import com.marinamooringmanagement.model.response.TechnicianUserResponseDto;
import com.marinamooringmanagement.model.response.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper interface to map User entity to UserDto and UserRequestDto and vice versa.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    /**
     * Maps a User entity to a UserDto.
     *
     * @param dto  empty UserDto.
     * @param user the User entity to map.
     * @return the mapped UserDto.
     */
    UserDto mapToUserDto(@MappingTarget UserDto dto, User user);

    /**
     * Maps a UserDto to a User entity.
     *
     * @param entity  the User entity to update.
     * @param userDto the User Dto with updated data.
     */
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "country", ignore = true)
    void mapToUser(@MappingTarget User entity, UserDto userDto);

    /**
     * Maps a User Request Dto to a User entity.
     *
     * @param entity         the User entity to update.
     * @param userRequestDto the User Request Dto with data to map.
     */
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "customerOwnerId", ignore = true)
    @Mapping(target = "companyName", ignore = true)
    void mapToUser(@MappingTarget User entity, UserRequestDto userRequestDto);

    /**
     * Maps a User entity to a UserResponseDto.
     *
     * @param dto  empty UserResponseDto.
     * @param user the User entity to map.
     * @return the mapped UserResponseDto.
     */
    @Mapping(target = "roleResponseDto", ignore = true)
    @Mapping(target = "stateResponseDto", ignore = true)
    @Mapping(target = "countryResponseDto", ignore = true)
    UserResponseDto mapToUserResponseDto(@MappingTarget UserResponseDto dto, User user);

    @Mapping(target = "roleResponseDto", ignore = true)
    @Mapping(target = "stateResponseDto", ignore = true)
    @Mapping(target = "countryResponseDto", ignore = true)
    @Mapping(target = "openWorkOrder", ignore = true)
    @Mapping(target = "closeWorkOrder", ignore = true)
    TechnicianUserResponseDto mapToTechnicianUserResponseDto(@MappingTarget TechnicianUserResponseDto dto, User user);
}
