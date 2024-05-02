package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.model.entity.Role;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.RoleResponseDto;
import com.marinamooringmanagement.repositories.RoleRepository;
import com.marinamooringmanagement.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public BasicRestResponse fetchRoles() {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            List<Role> roleList = roleRepository.findAll();

            if(roleList.isEmpty()) {
                log.error("No roles found in the database");

                throw new ResourceNotFoundException("No roles found in the database");
            }

            List<RoleResponseDto> roleResponseDtoList = roleList.stream().map(this::mapToRoleResponseDto).toList();

            response.setMessage("Roles fetched successfully");
            response.setContent(roleResponseDtoList);
            response.setStatus(HttpStatus.OK.value());

        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }


    public RoleResponseDto mapToRoleResponseDto(Role role) {
        return RoleResponseDto.builder()
                .name(role.getName())
                .build();
    }
}
