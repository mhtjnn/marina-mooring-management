package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.*;
import com.marinamooringmanagement.model.dto.*;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.BoatyardMetadataResponse;
import com.marinamooringmanagement.model.response.CustomerMetadataResponse;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.MetadataService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BoatyardRepository boatyardRepository;

    @Autowired
    private LoggedInUserUtil loggedInUserUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MooringStatusMapper mooringStatusMapper;

    @Autowired
    private BoatTypeMapper boatTypeMapper;

    @Autowired
    private SizeOfWeightMapper sizeOfWeightMapper;

    @Autowired
    private TypeOfWeightMapper typeOfWeightMapper;

    @Autowired
    private TopChainConditionMapper topChainConditionMapper;

    @Autowired
    private EyeConditionMapper eyeConditionMapper;

    @Autowired
    private BottomChainConditionMapper bottomChainConditionMapper;

    @Autowired
    private ShackleSwivelConditionMapper shackleSwivelConditionMapper;

    @Autowired
    private PennantConditionMapper pennantConditionMapper;

    @Autowired
    private InventoryTypeRepository inventoryTypeRepository;

    @Autowired
    private InventoryTypeMapper inventoryTypeMapper;

    @Override
    public BasicRestResponse fetchStatus(BaseSearchRequest baseSearchRequest) {
        Page<MooringStatus> content = null;
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            content = mooringStatusRepository.findAll(PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize()));

            List<MooringStatusDto> mooringStatusDtoList = content
                    .getContent()
                    .stream()
                    .map(mooringStatus -> mooringStatusMapper.mapToMooringStatusDto(MooringStatusDto.builder().build(), mooringStatus))
                    .toList();

            response.setMessage("Status fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(mooringStatusDtoList);

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

            List<BoatTypeDto> boatTypeDtoList = content
                    .getContent()
                    .stream()
                    .map(boatType -> boatTypeMapper.mapToBoatTypeDto(BoatTypeDto.builder().build(), boatType))
                    .toList();

            response.setMessage("Types of Boat fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(boatTypeDtoList);

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

            List<SizeOfWeightDto> sizeOfWeightDtoList = content
                    .getContent()
                    .stream()
                    .map(sizeOfWeight -> sizeOfWeightMapper.mapToSizeOfWeightDto(SizeOfWeightDto.builder().build(), sizeOfWeight))
                    .toList()
                    ;

            response.setMessage("Types of Boat fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(sizeOfWeightDtoList);

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

            List<TypeOfWeightDto> typeOfWeightDtoList = content
                    .getContent()
                    .stream()
                    .map(typeOfWeight -> typeOfWeightMapper.mapToTypeOfWeightDto(TypeOfWeightDto.builder().build(), typeOfWeight))
                    .toList();

            response.setMessage("Types of Boat fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(typeOfWeightDtoList);

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

            List<TopChainConditionDto> topChainConditionDtoList = content
                    .getContent()
                    .stream()
                    .map(topChainCondition ->  topChainConditionMapper.mapToTopChainConditionDto(TopChainConditionDto.builder().build(), topChainCondition))
                    .toList();

            response.setMessage("Types of Boat fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(topChainConditionDtoList);

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

            List<EyeConditionDto> eyeConditionDtoList = content
                    .getContent()
                    .stream()
                    .map(eyeCondition -> eyeConditionMapper.mapToEyeConditionDto(EyeConditionDto.builder().build(), eyeCondition))
                    .toList();

            response.setMessage("Types of Boat fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(eyeConditionDtoList);

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

            List<BottomChainConditionDto> bottomChainConditionDtoList = content
                    .getContent()
                    .stream()
                    .map(bottomChainCondition -> bottomChainConditionMapper.mapToBottomChainConditionDto(BottomChainConditionDto.builder().build(), bottomChainCondition))
                    .toList()
                    ;

            response.setMessage("Types of Boat fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(bottomChainConditionDtoList);

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

            List<ShackleSwivelConditionDto> shackleSwivelConditionDtoList = content
                    .getContent()
                    .stream()
                    .map(shackleSwivelCondition -> shackleSwivelConditionMapper.mapToShackleSwivelConditionDto(ShackleSwivelConditionDto.builder().build(), shackleSwivelCondition))
                    .toList()
                    ;

            response.setMessage("Types of Boat fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(shackleSwivelConditionDtoList);

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

            List<PennantConditionDto> pennantConditionDtoList = content
                    .getContent()
                    .stream()
                    .map(pennantCondition -> pennantConditionMapper.mapToPennantConditionDto(PennantConditionDto.builder().build(), pennantCondition))
                    .toList();

            response.setMessage("Types of Boat fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(pennantConditionDtoList);

        } catch (Exception ex) {
            response.setMessage(ex.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchCustomers(
            final BaseSearchRequest baseSearchRequest,
            final HttpServletRequest request
            ) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();
            final Integer loggedInUserId = loggedInUserUtil.getLoggedInUserID();

            Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

            if (loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
                if(customerOwnerId == -1) throw new RuntimeException("Please select a customer owner");
                Optional<User> optionalUser = userRepository.findById(customerOwnerId);
                if (optionalUser.isEmpty())
                    throw new ResourceNotFoundException(String.format("No user found with the given id: %1$s", customerOwnerId));
                if (!optionalUser.get().getRole().getName().equals(AppConstants.Role.CUSTOMER_OWNER))
                    throw new RuntimeException(String.format("User with the given id: %1$s is not of Customer Owner role.", customerOwnerId));
            } else if (loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
                if (customerOwnerId != -1 && !loggedInUserId.equals(customerOwnerId))
                    throw new RuntimeException("Cannot do operations on customer with different customer owner Id");
                Optional<User> optionalUser = userRepository.findById(loggedInUserId);
                if (optionalUser.isEmpty())
                    throw new ResourceNotFoundException(String.format("No user found with the given id: %1$s", customerOwnerId));
            } else {
                throw new RuntimeException("Not Authorized");
            }

            List<CustomerMetadataResponse> customerMetadataResponseList = customerRepository.findAll()
                    .stream()
                    .filter(customer -> customer.getUser().getId().equals((customerOwnerId == -1)?loggedInUserId:customerOwnerId))
                    .map(customer -> CustomerMetadataResponse.builder()
                            .id(customer.getId())
                            .customerName(customer.getCustomerName())
                            .build()
                    )
                    .toList();

            response.setMessage("Customers fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(customerMetadataResponseList);

        } catch (Exception ex) {
            response.setMessage(ex.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchBoatyards(
            final BaseSearchRequest baseSearchRequest,
            final HttpServletRequest request
    ) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();
            final Integer loggedInUserId = loggedInUserUtil.getLoggedInUserID();

            Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

            if (loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
                if(customerOwnerId == -1) throw new RuntimeException("Please select a customer owner");
                Optional<User> optionalUser = userRepository.findById(customerOwnerId);
                if (optionalUser.isEmpty())
                    throw new ResourceNotFoundException(String.format("No user found with the given id: %1$s", customerOwnerId));
                if (!optionalUser.get().getRole().getName().equals(AppConstants.Role.CUSTOMER_OWNER))
                    throw new RuntimeException(String.format("User with the given id: %1$s is not of Customer Owner role.", customerOwnerId));
            } else if (loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
                if (customerOwnerId != -1 && !loggedInUserId.equals(customerOwnerId))
                    throw new RuntimeException("Cannot do operations on customer with different customer owner Id");
                Optional<User> optionalUser = userRepository.findById(loggedInUserId);
                if (optionalUser.isEmpty())
                    throw new ResourceNotFoundException(String.format("No user found with the given id: %1$s", customerOwnerId));
            } else {
                throw new RuntimeException("Not Authorized");
            }

            List<BoatyardMetadataResponse> boatyardMetadataResponseList = boatyardRepository.findAll()
                    .stream()
                    .filter(boatyard -> boatyard.getUser().getId().equals((customerOwnerId == -1)?loggedInUserId:customerOwnerId))
                    .map(boatyard -> BoatyardMetadataResponse.builder()
                            .id(boatyard.getId())
                            .boatyardName(boatyard.getBoatyardName())
                            .build()
                    )
                    .toList();

            response.setMessage("Customers fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(boatyardMetadataResponseList);

        } catch (Exception ex) {
            response.setMessage(ex.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchInventoryType(BaseSearchRequest baseSearchRequest, HttpServletRequest request) {
        Page<InventoryType> content = null;
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            content = inventoryTypeRepository.findAll(PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize()));

            List<InventoryTypeDto> inventoryTypeDtoList = content
                    .getContent()
                    .stream()
                    .map(inventoryType -> inventoryTypeMapper.mapToInventoryTypeDto(InventoryTypeDto.builder().build(), inventoryType))
                    .toList()
                    ;

            response.setMessage("Types of inventory fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(inventoryTypeDtoList);

        } catch (Exception ex) {
            response.setMessage(ex.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }
}
