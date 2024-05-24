package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.RoleDto;
import com.marinamooringmanagement.model.entity.Role;
import com.marinamooringmanagement.model.response.RoleResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {

    RoleDto mapToRoleDto(@MappingTarget RoleDto roleDto, Role role);

    RoleResponseDto mapToRoleResponseDto(@MappingTarget RoleResponseDto roleResponseDto, Role role);

}
