package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.mapper.*;
import com.marinamooringmanagement.mapper.metadata.*;
import com.marinamooringmanagement.model.dto.metadata.*;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.entity.metadata.*;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.response.*;
import com.marinamooringmanagement.model.response.metadata.*;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.repositories.metadata.*;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.MetadataService;
import com.marinamooringmanagement.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private MooringRepository mooringRepository;

    @Autowired
    private BoatyardServiceImpl boatyardServiceImpl;

    @Autowired
    private CustomerServiceImpl customerServiceImpl;

    @Autowired
    private MooringServiceImpl mooringServiceImpl;

    @Autowired
    private WorkOrderStatusRepository workOrderStatusRepository;

    @Autowired
    private WorkOrderStatusMapper workOrderStatusMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CustomerTypeRepository customerTypeRepository;

    @Autowired
    private CustomerTypeMapper customerTypeMapper;

    @Autowired
    private ServiceAreaTypeRepository serviceAreaTypeRepository;

    @Autowired
    private ServiceAreaTypeMapper serviceAreaTypeMapper;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private QuickbookCustomerRepository quickbookCustomerRepository;

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
    public BasicRestResponse fetchCustomers(
            final BaseSearchRequest baseSearchRequest,
            final HttpServletRequest request
            ) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

            User user = authorizationUtil.checkAuthority(customerOwnerId);

            List<CustomerMetadataResponse> customerMetadataResponseList = customerRepository.findAll()
                    .stream()
                    .filter(customer -> customer.getUser().getId().equals(user.getId()))
                    .map(customer -> CustomerMetadataResponse.builder()
                            .id(customer.getId())
                            .firstName(customer.getFirstName())
                            .lastName(customer.getLastName())
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
            Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            List<BoatyardMetadataResponse> boatyardMetadataResponseList = boatyardRepository.findAll()
                    .stream()
                    .filter(boatyard -> boatyard.getUser().getId().equals(user.getId()))
                    .map(boatyard -> BoatyardMetadataResponse.builder()
                            .id(boatyard.getId())
                            .boatyardName(boatyard.getBoatyardName())
                            .build()
                    )
                    .toList();

            response.setMessage("Boatyards fetched successfully!!!");
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

    @Override
    public BasicRestResponse fetchCustomerOwners(final BaseSearchRequest baseSearchRequest, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            final String loggedInUserRole = loggedInUserUtil.getLoggedInUserRole();

            if(!StringUtils.equals(loggedInUserRole, AppConstants.Role.ADMINISTRATOR)) throw new RuntimeException("No Authorized to fetch Customer Owners");

            return userService.fetchUsers(baseSearchRequest, "", request);

        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchMooringsBasedOnCustomerId(BaseSearchRequest baseSearchRequest, Integer customerId, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            final User user =  authorizationUtil.checkAuthority(customerOwnerId);

            final Customer customer = customerServiceImpl.fetchCustomerById(customerId, request);

            List<Mooring> mooringList = customer.getMooringList();
            List<MooringMetadataResponse> mooringMetadataResponseList = mooringList
                    .stream()
                    .filter(mooring -> mooring.getUser().getId().equals(user.getId()))
                    .map(mooring -> {
                        MooringMetadataResponse mooringMetadataResponse = MooringMetadataResponse.builder().build();
                        if(null != mooring.getId()) mooringMetadataResponse.setId(mooring.getId());
                        if(null != mooring.getMooringNumber()) mooringMetadataResponse.setMooringNumber(mooring.getMooringNumber());
                        return mooringMetadataResponse;
                    })
                    .toList();

            response.setContent(mooringMetadataResponseList);
            response.setMessage(String.format("Moorings associated with customer of id: %1$s are fetched successfully", customerId));
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchMooringsBasedOnBoatyardId(BaseSearchRequest baseSearchRequest, Integer boatyardId, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            final User user =  authorizationUtil.checkAuthority(customerOwnerId);

            final Boatyard boatyard = boatyardServiceImpl.fetchBoatyardById(boatyardId, request);

            List<Mooring> mooringList = boatyard.getMooringList();
            List<MooringMetadataResponse> mooringMetadataResponseList = mooringList
                    .stream()
                    .filter(mooring -> mooring.getUser().getId().equals(user.getId()))
                    .map(mooring -> {
                        MooringMetadataResponse mooringMetadataResponse = MooringMetadataResponse.builder().build();
                        if(null != mooring.getId()) mooringMetadataResponse.setId(mooring.getId());
                        if(null != mooring.getMooringNumber()) mooringMetadataResponse.setMooringNumber(mooring.getMooringNumber());
                        return mooringMetadataResponse;
                    })
                    .toList();

            response.setContent(mooringMetadataResponseList);
            response.setMessage(String.format("Moorings associated with boatyard of id: %1$s are fetched successfully", boatyardId));
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    @Transactional
    public BasicRestResponse fetchCustomerBasedOnMooringId(BaseSearchRequest baseSearchRequest, Integer mooringId, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            final Mooring mooring = mooringServiceImpl.fetchMooringById(mooringId, request);

            if(null == mooring.getCustomer()) throw new RuntimeException(String.format("Mooring with the given id: %1$s is associated with no customer", mooringId));
            final Customer customer = mooring.getCustomer();

            if(null == customer.getUser()) throw new RuntimeException(String.format("Customer with the id: %1$s is associated with no user", customer.getId()));
            if(!customer.getUser().getId().equals(user.getId())) throw new RuntimeException(String.format("Customer with given id: %1$s is associated with some other user", customer.getId()));

            List<CustomerMetadataResponse> customerMetadataResponseList = new ArrayList<>();
            CustomerMetadataResponse customerMetadataResponse = CustomerMetadataResponse.builder().build();
            if(null != customer.getId()) customerMetadataResponse.setId(customer.getId());
            if(null != customer.getFirstName()) customerMetadataResponse.setFirstName(customer.getFirstName());
            if(null != customer.getLastName()) customerMetadataResponse.setLastName(customer.getLastName());

            customerMetadataResponseList.add(customerMetadataResponse);

            response.setStatus(HttpStatus.OK.value());
            response.setContent(customerMetadataResponseList);
            response.setMessage(String.format("Customer associated with mooring of id: %1$s is fetched successfully", mooringId));

        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchBoatyardBasedOnMooringId(BaseSearchRequest baseSearchRequest, Integer mooringId, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            final Mooring mooring = mooringServiceImpl.fetchMooringById(mooringId, request);

            if(null == mooring.getBoatyard()) throw new RuntimeException(String.format("Mooring with the given id: %1$s is associated with no boatyard", mooringId));
            final Boatyard boatyard = mooring.getBoatyard();

            if(null == boatyard.getUser()) throw new RuntimeException(String.format("Boatyard with the id: %1$s is associated with no user", boatyard.getId()));
            if(!boatyard.getUser().getId().equals(user.getId())) throw new RuntimeException(String.format("Boatyard with given id: %1$s is associated with some other user", boatyard.getId()));

            List<BoatyardMetadataResponse> boatyardMetadataResponseList = new ArrayList<>();
            BoatyardMetadataResponse boatyardMetadataResponse = BoatyardMetadataResponse.builder().build();
            if(null != boatyard.getId()) boatyardMetadataResponse.setId(boatyard.getId());
            if(null != boatyard.getBoatyardName()) boatyardMetadataResponse.setBoatyardName(boatyard.getBoatyardName());

            boatyardMetadataResponseList.add(boatyardMetadataResponse);

            response.setStatus(HttpStatus.OK.value());
            response.setContent(boatyardMetadataResponseList);
            response.setMessage(String.format("Boatyard associated with mooring of id: %1$s is fetched successfully", mooringId));

        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchMooringBasedOnCustomerIdAndMooringId(BaseSearchRequest baseSearchRequest, Integer customerId, Integer boatyardId, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Customer customer = customerServiceImpl.fetchCustomerById(customerId, request);
            final Boatyard boatyard = boatyardServiceImpl.fetchBoatyardById(boatyardId, request);

            if(!customer.getUser().getId().equals(boatyard.getUser().getId())) throw new RuntimeException(String.format("Customer with id: %1$s and boatyard with id: %2$s are of different customer owners", customerId, boatyardId));

            List<Mooring> mooringList = customer.getMooringList();
            if(null == mooringList || mooringList.isEmpty()) throw new RuntimeException(String.format("No mooring is associated with customer of id: %1$s", customerId));

            List<MooringMetadataResponse> mooringMetadataResponseList = mooringList
                    .stream()
                    .filter(
                            mooring -> null != mooring.getCustomer()
                                    && null != mooring.getBoatyard()
                                    && mooring.getCustomer().getId().equals(customerId)
                                    && mooring.getBoatyard().getId().equals(boatyardId)
                    )
                    .map(mooring -> {
                        final MooringMetadataResponse mooringMetadataResponse = MooringMetadataResponse.builder().build();
                        if(null != mooring.getId()) mooringMetadataResponse.setId(mooring.getId());
                        if(null != mooring.getMooringNumber()) mooringMetadataResponse.setMooringNumber(mooring.getMooringNumber());

                        return mooringMetadataResponse;
                    })
                    .toList();


            response.setMessage(String.format("Moorings with customer Id as: %1$s and boatyard Id as: %2$s fetched successfully", customerId, boatyardId));
            response.setContent(mooringMetadataResponseList);
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchMooringIds(BaseSearchRequest baseSearchRequest, HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            List<MooringMetadataResponse> mooringMetadataResponseList = mooringRepository.findAll()
                    .stream()
                    .filter(mooring -> null != mooring.getUser() && mooring.getUser().getId().equals(user.getId()))
                    .map(mooring -> MooringMetadataResponse.builder()
                            .id(mooring.getId())
                            .mooringNumber(mooring.getMooringNumber())
                            .build()
                    )
                    .toList();

            response.setMessage("Moorings fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(mooringMetadataResponseList);

        } catch (Exception ex) {
            response.setMessage(ex.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchTechnicians(BaseSearchRequest baseSearchRequest, HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            List<UserMetadataResponse> UserMetadataResponseList = userRepository.findAll()
                    .stream()
                    .filter(
                            user1 -> null != user1.getRole()
                                    && null != user1.getRole().getName()
                                    && user1.getRole().getName().equals(AppConstants.Role.TECHNICIAN)
                                    && null != user1.getCustomerOwnerId()
                                    && user1.getCustomerOwnerId().equals(user.getId())
                    )
                    .map(user1 -> {
                        UserMetadataResponse userMetadataResponse = UserMetadataResponse.builder().build();
                        if(null != user1.getId()) userMetadataResponse.setId(user1.getId());
                        if(null != user1.getName()) userMetadataResponse.setName(user1.getName());
                        return userMetadataResponse;
                    })
                    .toList();

            response.setMessage("Technicians fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(UserMetadataResponseList);

        } catch (Exception ex) {
            response.setMessage(ex.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchWorkOrderStatus(BaseSearchRequest baseSearchRequest, HttpServletRequest request) {
        Page<WorkOrderStatus> content = null;
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            content = workOrderStatusRepository.findAll(PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize()));

            List<WorkOrderStatusDto> workOrderStatusDtoList = new ArrayList<>();
            workOrderStatusDtoList = content
                    .getContent()
                    .stream()
                    .map(workOrderStatus -> workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), workOrderStatus))
                    .toList();

            response.setContent(workOrderStatusDtoList);

            response.setMessage("Work order status fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());


        } catch (Exception ex) {
            response.setMessage(ex.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchCustomerTypes(BaseSearchRequest baseSearchRequest) {
        Page<CustomerType> content = null;
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            content = customerTypeRepository.findAll(PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize()));

            List<CustomerTypeDto> customerTypeDtoList = content
                    .getContent()
                    .stream()
                    .map(customerType -> customerTypeMapper.toDto(CustomerTypeDto.builder().build(), customerType))
                    .toList()
                    ;

            response.setMessage("Types of customer fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(customerTypeDtoList);

        } catch (Exception ex) {
            response.setMessage(ex.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchServiceAreaTypes(BaseSearchRequest baseSearchRequest) {
        Page<ServiceAreaType> content = null;
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            content = serviceAreaTypeRepository.findAll(PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize()));

            List<ServiceAreaTypeDto> serviceAreaTypeDtoList = content
                    .getContent()
                    .stream()
                    .map(serviceAreaType -> serviceAreaTypeMapper.toDto(ServiceAreaTypeDto.builder().build(), serviceAreaType))
                    .toList()
                    ;

            response.setMessage("Types of service areas fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(serviceAreaTypeDtoList);

        } catch (Exception ex) {
            response.setMessage(ex.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchServiceAreas(BaseSearchRequest baseSearchRequest, HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            List<ServiceAreaMetadataResponse> serviceAreaMetadataResponseList = serviceAreaRepository.findAll()
                    .stream()
                    .filter(serviceArea -> serviceArea.getUser().getId().equals(user.getId()))
                    .map(serviceArea -> ServiceAreaMetadataResponse.builder()
                            .id(serviceArea.getId())
                            .serviceAreaName(serviceArea.getServiceAreaName())
                            .build()
                    )
                    .toList();

            response.setMessage("Service areas fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(serviceAreaMetadataResponseList);

        } catch (Exception ex) {
            response.setMessage(ex.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchQuickbookCustomers(final BaseSearchRequest baseSearchRequest, final HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            List<QuickBookCustomerMetadataResponse> quickBookCustomerMetadataResponseList = quickbookCustomerRepository.findAll()
                    .stream()
                    .filter(quickbookCustomer -> {
                        if(null != quickbookCustomer.getUser() && null != quickbookCustomer.getUser().getId())
                            return quickbookCustomer.getUser().getId().equals(user.getId());
                        return false;
                    })
                    .map(quickbookCustomer -> QuickBookCustomerMetadataResponse.builder()
                            .id(quickbookCustomer.getId())
                            .quickbookCustomerName(quickbookCustomer.getQuickbookCustomerName())
                            .quickbookCustomerId(quickbookCustomer.getQuickbookCustomerId())
                            .build()
                    )
                    .toList();

            response.setMessage("Quickbook customer fetched successfully!!!");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(quickBookCustomerMetadataResponseList);

        } catch (Exception ex) {
            response.setMessage(ex.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }
}
