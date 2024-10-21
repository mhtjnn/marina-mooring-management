package com.marinamooringmanagement.mapper;

import com.marinamooringmanagement.model.dto.NotificationDto;
import com.marinamooringmanagement.model.entity.Notification;
import com.marinamooringmanagement.model.response.NotificationResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationMapper {

    Notification toEntity(@MappingTarget Notification notification, NotificationDto notificationDto);

    NotificationDto toDto(@MappingTarget NotificationDto notificationDto, Notification notification);

    NotificationResponseDto toResponseDto(@MappingTarget NotificationResponseDto notificationResponseDto, Notification notification);

}
