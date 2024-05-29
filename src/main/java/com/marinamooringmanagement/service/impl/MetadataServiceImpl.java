package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class MetadataServiceImpl implements MetadataService {

    @Autowired
    private MooringStatusRepository mooringStatusRepository;

    @Autowired
    private BoatTypeRepository boatTypeRepository;

    @Autowired
    private SizeOfWeightRepository sizeOfWeightRepository;

    @Autowired
    private TypeOfWeightRepository typeOfWeightRepository;

    @Autowired
    private TopChainConditionRepository topChainConditionRepository;

    @Autowired
    private EyeConditionRepository eyeConditionRepository;

    @Autowired
    private BottomChainConditionRepository bottomChainConditionRepository;

    @Autowired
    private ShackleSwivelConditionRepository shackleSwivelConditionRepository;

    @Autowired
    private PennantConditionRepository pennantConditionRepository;

    @Override
    public BasicRestResponse fetchStatus(BaseSearchRequest baseSearchRequest) {
        Page<MooringStatus> content = null;
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            content = mooringStatusRepository.findAll(PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize()));

            response.setMessage("Status fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(content);

        } catch (Exception ex) {
            response.setMessage(ex.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchBoatType(BaseSearchRequest baseSearchRequest) {
        Page<BoatType> content = null;
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            content = boatTypeRepository.findAll(PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize()));

            response.setMessage("Types of Boat fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(content);

        } catch (Exception ex) {
            response.setMessage(ex.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchSizeOfWeight(BaseSearchRequest baseSearchRequest) {
        Page<SizeOfWeight> content = null;
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            content = sizeOfWeightRepository.findAll(PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize()));

            response.setMessage("Types of Boat fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(content);

        } catch (Exception ex) {
            response.setMessage(ex.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchTypeOfWeight(BaseSearchRequest baseSearchRequest) {
        Page<TypeOfWeight> content = null;
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            content = typeOfWeightRepository.findAll(PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize()));

            response.setMessage("Types of Boat fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(content);

        } catch (Exception ex) {
            response.setMessage(ex.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchTopChainCondition(BaseSearchRequest baseSearchRequest) {
        Page<TopChainCondition> content = null;
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            content = topChainConditionRepository.findAll(PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize()));

            response.setMessage("Types of Boat fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(content);

        } catch (Exception ex) {
            response.setMessage(ex.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchEyeConditions(BaseSearchRequest baseSearchRequest) {
        Page<EyeCondition> content = null;
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            content = eyeConditionRepository.findAll(PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize()));

            response.setMessage("Types of Boat fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(content);

        } catch (Exception ex) {
            response.setMessage(ex.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchBottomChainConditions(BaseSearchRequest baseSearchRequest) {
        Page<BottomChainCondition> content = null;
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            content = bottomChainConditionRepository.findAll(PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize()));

            response.setMessage("Types of Boat fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(content);

        } catch (Exception ex) {
            response.setMessage(ex.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchShackleSwivelConditions(BaseSearchRequest baseSearchRequest) {
        Page<ShackleSwivelCondition> content = null;
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            content = shackleSwivelConditionRepository.findAll(PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize()));

            response.setMessage("Types of Boat fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(content);

        } catch (Exception ex) {
            response.setMessage(ex.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchPennantConditions(BaseSearchRequest baseSearchRequest) {
        Page<PennantCondition> content = null;
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            content = pennantConditionRepository.findAll(PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize()));

            response.setMessage("Types of Boat fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(content);

        } catch (Exception ex) {
            response.setMessage(ex.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }
}
