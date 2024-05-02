package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.mapper.StateMapper;
import com.marinamooringmanagement.model.dto.StateDto;
import com.marinamooringmanagement.model.entity.State;
import com.marinamooringmanagement.model.request.StateSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.StateResponseDto;
import com.marinamooringmanagement.repositories.StateRepository;
import com.marinamooringmanagement.service.StateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StateServiceImpl implements StateService {

    private static final Logger log = LoggerFactory.getLogger(StateServiceImpl.class);

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private StateMapper stateMapper;

    @Override
    public BasicRestResponse fetchStates(StateSearchRequest stateSearchRequest) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Pageable p = PageRequest.of(stateSearchRequest.getPageNumber(), stateSearchRequest.getPageSize(), stateSearchRequest.getSort());
            final Page<State> stateList = stateRepository.findAll(p);
            log.info("fetch all users");
            List<StateResponseDto> userResponseDtoList = new ArrayList<>();
            if (!stateList.isEmpty())
                userResponseDtoList = stateList.getContent().stream().map(state -> stateMapper.mapToStateResponseDto(StateResponseDto.builder().build(), state)).collect(Collectors.toList());
            response.setMessage("States fetched Successfully");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(userResponseDtoList);
        } catch (Exception e) {
            response.setMessage("Error Occurred while fetching state from the database");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setErrorList(List.of(e.getMessage()));
        }
        return response;
    }

    @Override
    public BasicRestResponse saveState(StateDto stateDto) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final State state = State.builder().build();
            final Optional<State> optionalState = stateRepository.findByName(stateDto.getName());

            if (optionalState.isPresent()) {
                log.info(String.format("State already present in DB"));
                throw new RuntimeException("State already present in DB");
            }

            log.info(String.format("Saving state in DB"));
            performSave(stateDto, state, null);

            response.setMessage("State saved successfully");
            response.setStatus(HttpStatus.CREATED.value());

        } catch (Exception e) {
            response.setMessage("Error Occurred while saving state");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setErrorList(List.of(e.getMessage()));
        }
        return response;
    }

    @Override
    public BasicRestResponse updateState(StateDto stateDto, Integer stateId) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Optional<State> optionalState = stateRepository.findById(stateId);

            if (optionalState.isEmpty()) {
                log.info(String.format("State not present in DB"));
                throw new RuntimeException("State not present in DB");
            }

            final State state = optionalState.get();

            log.info(String.format("Updating state in DB"));
            performSave(stateDto, state, stateId);

            response.setMessage("State updated successfully");
            response.setStatus(HttpStatus.CREATED.value());

        } catch (Exception e) {
            response.setMessage("Error Occurred while updating state");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setErrorList(List.of(e.getMessage()));
        }
        return response;
    }

    @Override
    public BasicRestResponse deleteState(Integer id) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info(String.format("delete state with given ID"));
            stateRepository.deleteById(id);
            response.setMessage("State Deleted Successfully!!!");
            response.setStatus(200);
        } catch (Exception e) {
            response.setMessage("Error occurred while deleting the state");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setErrorList(List.of(e.getMessage()));
        }
        return response;
    }

    public State performSave(final StateDto stateDto, final State state, final Integer userId) {
        try {
            stateMapper.mapToState(state, stateDto);
            state.setLastModifiedDate(new Date(System.currentTimeMillis()));
            return stateRepository.save(state);
        } catch (Exception e) {
            log.error("Error occurred during perform save method {}", e.getLocalizedMessage());
            throw new RuntimeException("Error occurred during perform save method", e);
        }
    }
}

