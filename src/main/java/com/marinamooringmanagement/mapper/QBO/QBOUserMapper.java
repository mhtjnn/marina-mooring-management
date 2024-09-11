package com.marinamooringmanagement.mapper.QBO;

import com.marinamooringmanagement.model.dto.QBO.QBOUserDto;
import com.marinamooringmanagement.model.entity.QBO.QBOUser;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface QBOUserMapper {

     QBOUser toEntity(@MappingTarget QBOUser qboUser, QBOUserDto qboUserDto);

     QBOUserDto toDto(@MappingTarget QBOUserDto qboUserDto, QBOUser qboUser);

}
