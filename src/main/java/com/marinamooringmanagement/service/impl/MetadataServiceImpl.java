package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.BoatyardMetadataResponse;
import com.marinamooringmanagement.model.response.CustomerMetadataResponse;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.security.config.LoggedInUserUtil;
import com.marinamooringmanagement.service.MetadataService;
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

    @Override
    public BasicRestResponse fetchCustomers(
            final BaseSearchRequest baseSearchRequest,
            final Integer customerOwnerId
    ) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();
            final Integer loggedInUserId = loggedInUserUtil.getLoggedInUserID();

            if(loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
                if(null == customerOwnerId) throw new RuntimeException("Please select a customer owner");
                Optional<User> optionalUser = userRepository.findById(customerOwnerId);
                if(optionalUser.isEmpty()) throw new ResourceNotFoundException(String.format("No user found with the given id: %1$s", customerOwnerId));
                if(!optionalUser.get().getRole().getName().equals(AppConstants.Role.CUSTOMER_OWNER)) throw new RuntimeException(String.format("User with the given id: %1$s is not of Customer Owner role.", customerOwnerId));
            } else if(loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)){
                if(null != customerOwnerId && !loggedInUserId.equals(customerOwnerId)) throw new RuntimeException("Cannot do operations on boatyard with different customer owner Id");
                Optional<User> optionalUser = userRepository.findById(loggedInUserId);
                if(optionalUser.isEmpty()) throw new ResourceNotFoundException(String.format("No user found with the given id: %1$s", customerOwnerId));
            } else {
                throw new RuntimeException("Not Authorized");
            }


            List<CustomerMetadataResponse> customerMetadataResponseList = customerRepository.findAll()
                    .stream()
                    .filter(customer -> customer.getUser().getId().equals((customerOwnerId == null)?loggedInUserId:customerOwnerId))
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
            final Integer customerOwnerId
    ) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();
            final Integer loggedInUserId = loggedInUserUtil.getLoggedInUserID();

            if(loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
                if(null == customerOwnerId) throw new RuntimeException("Please select a customer owner");
                Optional<User> optionalUser = userRepository.findById(customerOwnerId);
                if(optionalUser.isEmpty()) throw new ResourceNotFoundException(String.format("No user found with the given id: %1$s", customerOwnerId));
                if(!optionalUser.get().getRole().getName().equals(AppConstants.Role.CUSTOMER_OWNER)) throw new RuntimeException(String.format("User with the given id: %1$s is not of Customer Owner role.", customerOwnerId));
            } else if(loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)){
                if(null != customerOwnerId && !loggedInUserId.equals(customerOwnerId)) throw new RuntimeException("Cannot do operations on boatyard with different customer owner Id");
                Optional<User> optionalUser = userRepository.findById(loggedInUserId);
                if(optionalUser.isEmpty()) throw new ResourceNotFoundException(String.format("No user found with the given id: %1$s", customerOwnerId));
            } else {
                throw new RuntimeException("Not Authorized");
            }

            List<BoatyardMetadataResponse> boatyardMetadataResponseList = boatyardRepository.findAll()
                    .stream()
                    .filter(boatyard -> boatyard.getUser().getId().equals((customerOwnerId == null)?loggedInUserId:customerOwnerId))
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
}
