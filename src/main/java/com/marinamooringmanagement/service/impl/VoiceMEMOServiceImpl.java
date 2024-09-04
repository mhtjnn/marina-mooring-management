package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.VoiceMEMOMapper;
import com.marinamooringmanagement.model.entity.VoiceMEMO;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.VoiceMEMOResponseDto;
import com.marinamooringmanagement.repositories.VoiceMEMORepository;
import com.marinamooringmanagement.service.VoiceMEMOService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Base64;

@Service
public class VoiceMEMOServiceImpl implements VoiceMEMOService {

    @Autowired
    private VoiceMEMORepository voiceMEMORepository;

    @Autowired
    private VoiceMEMOMapper voiceMEMOMapper;

    @Override
    public BasicRestResponse fetchVoiceMEMO(Integer id, HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            VoiceMEMO voiceMEMO = voiceMEMORepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("No voice MEMO found with the given id: %1$s", id)));

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
}
