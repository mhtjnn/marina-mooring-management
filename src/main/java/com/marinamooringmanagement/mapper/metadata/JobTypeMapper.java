package com.marinamooringmanagement.mapper.metadata;

import com.marinamooringmanagement.model.dto.metadata.JobTypeDto;
import com.marinamooringmanagement.model.entity.metadata.JobType;
import com.marinamooringmanagement.model.response.metadata.JobTypeResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface JobTypeMapper {

    JobType toEntity(@MappingTarget JobType jobType, JobTypeDto jobTypeDto);

    JobTypeDto toDto(@MappingTarget JobTypeDto jobTypeDto, JobType jobType);

    JobTypeResponseDto toResponseDto(@MappingTarget JobTypeResponseDto jobTypeResponseDto, JobType jobType);

}
