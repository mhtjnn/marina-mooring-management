package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.VoiceMEMOMapper;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.entity.VoiceMEMO;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.VoiceMEMOResponseDto;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.repositories.VoiceMEMORepository;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.VoiceMEMOService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Base64;

@Service
public class VoiceMEMOServiceImpl implements VoiceMEMOService {

    private static final Logger log = LoggerFactory.getLogger(VoiceMEMOServiceImpl.class);

    @Autowired
    private VoiceMEMORepository voiceMEMORepository;

    @Autowired
    private VoiceMEMOMapper voiceMEMOMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Override
    public BasicRestResponse fetchVoiceMEMO(Integer id, HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user;

            if(StringUtils.equals(LoggedInUserUtil.getLoggedInUserRole(), AppConstants.Role.TECHNICIAN)){
                final User technicianUser = userRepository.findUserByIdWithoutImage(LoggedInUserUtil.getLoggedInUserID())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No technician user found with the given id: %1$s", LoggedInUserUtil.getLoggedInUserID())));

                user = userRepository.findUserByIdWithoutImage(technicianUser.getCustomerOwnerId())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No customer owner user found with the given id: %1$s", technicianUser.getCustomerOwnerId())));

            } else {
                user = authorizationUtil.checkAuthority(customerOwnerId);
            }

            VoiceMEMO voiceMEMO = voiceMEMORepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("No voice MEMO found with the given id: %1$s", id)));

            log.info(String.format("Downloading voice MEMO with id: %1$s", id));
            if(ObjectUtils.notEqual(user.getId(), voiceMEMO.getUser().getId())) {
                log.error(String.format("Voice MEMO with id: %1$s is associated with other user", id));
                throw new RuntimeException(String.format("Voice MEMO with id: %1$s is associated with other user", id));
            }

            VoiceMEMOResponseDto voiceMEMOResponseDto = voiceMEMOMapper.toResponseDto(VoiceMEMOResponseDto.builder().build(), voiceMEMO);
            String encodedData = Base64.getEncoder().encodeToString(voiceMEMO.getData());
            voiceMEMOResponseDto.setEncodedData(encodedData);

            response.setStatus(HttpStatus.OK.value());
            response.setContent(voiceMEMOResponseDto);
            response.setMessage(String.format("Voice MEMO with the id: %1$s fetched successfully!!!", id));
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @Override
    public BasicRestResponse deleteVoiceMEMO(Integer id, HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            User user;
            User technicianUser = null;
            if(StringUtils.equals(LoggedInUserUtil.getLoggedInUserRole(), AppConstants.Role.TECHNICIAN)) {
                technicianUser = userRepository.findUserByIdWithoutImage(LoggedInUserUtil.getLoggedInUserID())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No technician found with the given id: %1$s", LoggedInUserUtil.getLoggedInUserID())));

                if(null != technicianUser.getCustomerOwnerId()) customerOwnerId = technicianUser.getCustomerOwnerId();

                Integer finalCustomerOwnerId = customerOwnerId;

                user = userRepository.findUserByIdWithoutImage(technicianUser.getCustomerOwnerId())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No customer owner found with the given id: %1$s", finalCustomerOwnerId)));

            } else {
                user = authorizationUtil.checkAuthority(customerOwnerId);
            }

            VoiceMEMO voiceMEMO = voiceMEMORepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("No voice MEMO found with the given id: %1$s", id)));
            log.info(String.format("Deleting voice MEMO with id: %1$s", id));

            if(null != technicianUser && null != voiceMEMO.getWorkOrder() && ObjectUtils.notEqual(technicianUser.getId(), voiceMEMO.getWorkOrder().getTechnicianUser().getId())) {
                log.error(String.format("Voice MEMO with id: %1$s is associated with other work order", id));
                throw new RuntimeException(String.format("Voice MEMO with id: %1$s is associated with other work order", id));
            }

            if(null != voiceMEMO.getUser() && ObjectUtils.notEqual(user.getId(), voiceMEMO.getUser().getId())) {
                log.error(String.format("Voice MEMO with id: %1$s is associated with other user", id));
                throw new RuntimeException(String.format("Voice MEMO with id: %1$s is associated with other user", id));
            }

            voiceMEMORepository.delete(voiceMEMO);
            log.info(String.format("Voice MEMO with id: %1$s is deleted successfully!!!", id));
            response.setMessage(String.format("Voice MEMO with id: %1$s is deleted successfully!!!", id));
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }
}
