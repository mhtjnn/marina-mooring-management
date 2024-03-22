package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.RoleDto;
import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.entity.Role;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.request.UserRequestDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-03-22T14:43:52+0530",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 20.0.1 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto mapToUserDto(UserDto dto, User user) {
        if ( user == null ) {
            return dto;
        }

        if ( user.getCreationDate() != null ) {
            dto.setCreationDate( user.getCreationDate() );
        }
        if ( user.getCreatedBy() != null ) {
            dto.setCreatedBy( user.getCreatedBy() );
        }
        if ( user.getLastModifiedBy() != null ) {
            dto.setLastModifiedBy( user.getLastModifiedBy() );
        }
        if ( user.getId() != null ) {
            dto.setId( user.getId() );
        }
        if ( user.getFirstname() != null ) {
            dto.setFirstname( user.getFirstname() );
        }
        if ( user.getLastname() != null ) {
            dto.setLastname( user.getLastname() );
        }
        if ( user.getEmail() != null ) {
            dto.setEmail( user.getEmail() );
        }
        if ( user.getPhoneNumber() != null ) {
            dto.setPhoneNumber( user.getPhoneNumber() );
        }
        if ( user.getPassword() != null ) {
            dto.setPassword( user.getPassword() );
        }
        if ( user.getRole() != null ) {
            if ( dto.getRole() == null ) {
                dto.setRole( RoleDto.builder().build() );
            }
            roleToRoleDto( user.getRole(), dto.getRole() );
        }

        return dto;
    }

    @Override
    public void mapToUser(User entity, UserDto userDto) {
        if ( userDto == null ) {
            return;
        }

        if ( userDto.getCreationDate() != null ) {
            entity.setCreationDate( userDto.getCreationDate() );
        }
        if ( userDto.getCreatedBy() != null ) {
            entity.setCreatedBy( userDto.getCreatedBy() );
        }
        if ( userDto.getLastModifiedBy() != null ) {
            entity.setLastModifiedBy( userDto.getLastModifiedBy() );
        }
        if ( userDto.getId() != null ) {
            entity.setId( userDto.getId() );
        }
        if ( userDto.getFirstname() != null ) {
            entity.setFirstname( userDto.getFirstname() );
        }
        if ( userDto.getLastname() != null ) {
            entity.setLastname( userDto.getLastname() );
        }
        if ( userDto.getEmail() != null ) {
            entity.setEmail( userDto.getEmail() );
        }
        if ( userDto.getPhoneNumber() != null ) {
            entity.setPhoneNumber( userDto.getPhoneNumber() );
        }
        if ( userDto.getPassword() != null ) {
            entity.setPassword( userDto.getPassword() );
        }
        if ( userDto.getRole() != null ) {
            if ( entity.getRole() == null ) {
                entity.setRole( Role.builder().build() );
            }
            roleDtoToRole( userDto.getRole(), entity.getRole() );
        }
    }

    @Override
    public void mapToUser(User entity, UserRequestDto userRequestDto) {
        if ( userRequestDto == null ) {
            return;
        }

        if ( userRequestDto.getId() != null ) {
            entity.setId( userRequestDto.getId() );
        }
        if ( userRequestDto.getFirstname() != null ) {
            entity.setFirstname( userRequestDto.getFirstname() );
        }
        if ( userRequestDto.getLastname() != null ) {
            entity.setLastname( userRequestDto.getLastname() );
        }
        if ( userRequestDto.getEmail() != null ) {
            entity.setEmail( userRequestDto.getEmail() );
        }
        if ( userRequestDto.getPhoneNumber() != null ) {
            entity.setPhoneNumber( userRequestDto.getPhoneNumber() );
        }
        if ( userRequestDto.getPassword() != null ) {
            entity.setPassword( userRequestDto.getPassword() );
        }
    }

    protected void roleToRoleDto(Role role, RoleDto mappingTarget) {
        if ( role == null ) {
            return;
        }

        if ( role.getId() != null ) {
            mappingTarget.setId( role.getId() );
        }
        if ( role.getCreationDate() != null ) {
            mappingTarget.setCreationDate( role.getCreationDate() );
        }
        if ( role.getCreatedBy() != null ) {
            mappingTarget.setCreatedBy( role.getCreatedBy() );
        }
        if ( role.getLastModifiedBy() != null ) {
            mappingTarget.setLastModifiedBy( role.getLastModifiedBy() );
        }
        if ( role.getName() != null ) {
            mappingTarget.setName( role.getName() );
        }
        if ( role.getDescription() != null ) {
            mappingTarget.setDescription( role.getDescription() );
        }
    }

    protected void roleDtoToRole(RoleDto roleDto, Role mappingTarget) {
        if ( roleDto == null ) {
            return;
        }

        if ( roleDto.getId() != null ) {
            mappingTarget.setId( roleDto.getId() );
        }
        if ( roleDto.getCreationDate() != null ) {
            mappingTarget.setCreationDate( roleDto.getCreationDate() );
        }
        if ( roleDto.getCreatedBy() != null ) {
            mappingTarget.setCreatedBy( roleDto.getCreatedBy() );
        }
        if ( roleDto.getLastModifiedBy() != null ) {
            mappingTarget.setLastModifiedBy( roleDto.getLastModifiedBy() );
        }
        if ( roleDto.getName() != null ) {
            mappingTarget.setName( roleDto.getName() );
        }
        if ( roleDto.getDescription() != null ) {
            mappingTarget.setDescription( roleDto.getDescription() );
        }
    }
}
