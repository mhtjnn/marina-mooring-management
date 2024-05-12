package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.request.UserRequestDto;
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
     * Function to map User entity to UserDto.
     *
     * @param dto  {@link UserDto} empty User Dto.
     * @param user {@link User} the User entity to map.
     * @return {@link UserDto} the mapped User Dto.
     */
    UserDto mapToUserDto(@MappingTarget UserDto dto, User user);

    /**
     * Function to map User Entity from User Dto.
     *
     * @param entity  {@link User} the User entity to update.
     * @param userDto {@link UserDto} the User Dto with updated data.
     */
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "country", ignore = true)
    void mapToUser(@MappingTarget User entity, UserDto userDto);

    /**
     * Function to map User Entity from User Request Dto.
     *
     * @param entity         {@link User} the User entity to update.
     * @param userRequestDto {@link UserRequestDto} the User Request Dto with data to map.
     */
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "password", ignore = true)
    void mapToUser(@MappingTarget User entity, UserRequestDto userRequestDto);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "country", ignore = true)
    UserResponseDto maptToUserResponseDto(@MappingTarget UserResponseDto dto, User user);
}


