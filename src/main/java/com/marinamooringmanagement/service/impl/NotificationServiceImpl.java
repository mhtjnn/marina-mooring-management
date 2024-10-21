package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.constants.enums.EntityEnum;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.*;
import com.marinamooringmanagement.mapper.metadata.CountryMapper;
import com.marinamooringmanagement.mapper.metadata.StateMapper;
import com.marinamooringmanagement.mapper.metadata.WorkOrderPayStatusMapper;
import com.marinamooringmanagement.mapper.metadata.WorkOrderStatusMapper;
import com.marinamooringmanagement.model.dto.metadata.WorkOrderPayStatusDto;
import com.marinamooringmanagement.model.dto.metadata.WorkOrderStatusDto;
import com.marinamooringmanagement.model.entity.Notification;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.entity.WorkOrder;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.response.*;
import com.marinamooringmanagement.model.response.metadata.CountryResponseDto;
import com.marinamooringmanagement.model.response.metadata.StateResponseDto;
import com.marinamooringmanagement.repositories.NotificationRepository;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.repositories.WorkOrderRepository;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.NotificationService;
import com.marinamooringmanagement.utils.DateUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private WorkOrderMapper workOrderMapper;

    @Autowired
    private MooringMapper mooringMapper;

    @Autowired
    private StateMapper stateMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CountryMapper countryMapper;

    @Autowired
    private BoatyardMapper boatyardMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkOrderStatusMapper workOrderStatusMapper;

    @Autowired
    private WorkOrderPayStatusMapper workOrderPayStatusMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Override
    public BasicRestResponse getNotifications(Integer id, BaseSearchRequest baseSearchRequest, String searchText, HttpServletRequest request) {

        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        try {

            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user;

            if(StringUtils.equals(LoggedInUserUtil.getLoggedInUserRole(), AppConstants.Role.TECHNICIAN)) {
                final User technicianUser = userRepository.findUserByIdWithoutImage(LoggedInUserUtil.getLoggedInUserID())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No user found with the given id: %1$s", LoggedInUserUtil.getLoggedInUserID())));

                user = userRepository.findUserByIdWithoutImage(technicianUser.getCustomerOwnerId())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No user found with the given id: %1$s", LoggedInUserUtil.getLoggedInUserID())));
            } else {
                user = authorizationUtil.checkAuthority(customerOwnerId);
            }

            Pageable pageable = PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize());
            Page<NotificationResponseDto> notificationResponseDtoPage = notificationRepository.findAll(id, pageable);

            List<CustomNotificationResponse> customNotificationResponseList;
            customNotificationResponseList = notificationResponseDtoPage.getContent()
                            .stream()
                    .filter(notificationResponseDto -> StringUtils.equals(notificationResponseDto.getEntityStr(), EntityEnum.WORK_ORDER.getEntityType()) && null != notificationResponseDto.getEntityId())
                    .map(notificationResponseDto -> {
                        CustomNotificationResponse customNotificationResponse = CustomNotificationResponse.builder().build();
                        if(null != notificationResponseDto.getId()) customNotificationResponse.setId(notificationResponseDto.getId());
                        if(null != notificationResponseDto.getNotificationMessage()) customNotificationResponse.setMessage(notificationResponseDto.getNotificationMessage());
                        if(null != notificationResponseDto.getCreationDate()) customNotificationResponse.setCreationDate(notificationResponseDto.getCreationDate());
                        customNotificationResponse.setNotificationRead(notificationResponseDto.isNotificationRead());
                        WorkOrderResponseDto workOrderResponseDto = null;
                        if(null != notificationResponseDto.getEntityId()) {
                            final Optional<WorkOrder> optionalWorkOrder;
                            if (StringUtils.equals(LoggedInUserUtil.getLoggedInUserRole(), AppConstants.Role.ADMINISTRATOR)
                                    || StringUtils.equals(LoggedInUserUtil.getLoggedInUserRole(), AppConstants.Role.CUSTOMER_OWNER)) {
                                optionalWorkOrder = workOrderRepository.findWorkOrderById(notificationResponseDto.getEntityId(), user.getId());
                            } else if (StringUtils.equals(LoggedInUserUtil.getLoggedInUserRole(), AppConstants.Role.TECHNICIAN)) {
                                optionalWorkOrder = workOrderRepository.findWorkOrderByIdUsingTechnicianLogin(notificationResponseDto.getEntityId(), LoggedInUserUtil.getLoggedInUserID());
                            } else {
                                throw new RuntimeException("No authorized");
                            }

                            if (optionalWorkOrder.isPresent()) {
                                final WorkOrder workOrder = optionalWorkOrder.get();

                                workOrderResponseDto = workOrderMapper.mapToWorkOrderResponseDto(WorkOrderResponseDto.builder().build(), workOrder);
                                if (null != workOrder.getMooring())
                                    workOrderResponseDto.setMooringResponseDto(mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), workOrder.getMooring()));
                                if (null != workOrder.getMooring() && null != workOrder.getMooring().getCustomer()) {
                                    CustomerResponseDto customerResponseDto = customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), workOrder.getMooring().getCustomer());
                                    if (null != workOrder.getMooring().getCustomer().getState()) {
                                        customerResponseDto.setStateResponseDto(stateMapper.mapToStateResponseDto(StateResponseDto.builder().build(), workOrder.getMooring().getCustomer().getState()));
                                    }
                                    if (null != workOrder.getMooring().getCustomer().getCountry()) {
                                        customerResponseDto.setCountryResponseDto(countryMapper.mapToCountryResponseDto(CountryResponseDto.builder().build(), workOrder.getMooring().getCustomer().getCountry()));
                                    }
                                    workOrderResponseDto.setCustomerResponseDto(customerResponseDto);
                                }
                                if (null != workOrder.getMooring() && null != workOrder.getMooring().getBoatyard())
                                    workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), workOrder.getMooring().getBoatyard()));
                                if (null != workOrder.getCustomerOwnerUser())
                                    workOrderResponseDto.setCustomerOwnerUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getCustomerOwnerUser()));
                                if (null != workOrder.getTechnicianUser())
                                    workOrderResponseDto.setTechnicianUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getTechnicianUser()));
                                if (null != workOrder.getWorkOrderStatus())
                                    workOrderResponseDto.setWorkOrderStatusDto(workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), workOrder.getWorkOrderStatus()));
                                if (null != workOrder.getWorkOrderPayStatus())
                                    workOrderResponseDto.setWorkOrderPayStatusDto(workOrderPayStatusMapper.toDto(WorkOrderPayStatusDto.builder().build(), workOrder.getWorkOrderPayStatus()));
                                if (null != workOrder.getDueDate())
                                    workOrderResponseDto.setDueDate(DateUtil.dateToString(workOrder.getDueDate()));
                                if (null != workOrder.getScheduledDate())
                                    workOrderResponseDto.setScheduledDate(DateUtil.dateToString(workOrder.getScheduledDate()));
                                if (null != workOrder.getCompletedDate())
                                    workOrderResponseDto.setCompletedDate(DateUtil.dateToString(workOrder.getCompletedDate()));
                            }

                            customNotificationResponse.setEntityObj(workOrderResponseDto);

                            return customNotificationResponse;
                        } else return null;
                    })
                    .filter(Objects::nonNull)
                    .toList();

            response.setContent(customNotificationResponseList);
            response.setMessage("Notifications fetched successfully.");
            response.setStatus(HttpStatus.OK.value());
            response.setTotalSize(notificationResponseDtoPage.getTotalElements());
            response.setCurrentSize(notificationResponseDtoPage.getNumberOfElements());

        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getLocalizedMessage());
        }

        return response;
    }

    @Override
    public void createNotificationForSaveWorkOrder(final WorkOrder workOrder) {
        Notification notification = Notification.builder().build();

        notification.setCreationDate(new Date(System.currentTimeMillis()));
        notification.setCreatedById(LoggedInUserUtil.getLoggedInUserID());
        notification.setSendToId(workOrder.getTechnicianUser().getId());
        notification.setNotificationMessage(String.format(AppConstants.NotificationMessageConstants.NEW_ASSIGNEE, EntityEnum.WORK_ORDER.getEntityType()));
        notification.setRead(false);
        notification.setEntityStr(EntityEnum.WORK_ORDER.getEntityType());
        notification.setEntityId(workOrder.getId());

        notificationRepository.save(notification);
    }

    @Override
    public void createNotificationForUpdateWorkOrder(final WorkOrder workOrder) {
        Notification notification = Notification.builder().build();

        notification.setCreationDate(new Date(System.currentTimeMillis()));
        notification.setCreatedById(LoggedInUserUtil.getLoggedInUserID());
        notification.setSendToId(workOrder.getTechnicianUser().getId());
        notification.setNotificationMessage(String.format(AppConstants.NotificationMessageConstants.UPDATED, EntityEnum.WORK_ORDER.getEntityType(), workOrder.getMooring().getMooringStatus().getStatus()));
        notification.setRead(false);
        notification.setEntityStr(EntityEnum.WORK_ORDER.getEntityType());
        notification.setEntityId(workOrder.getId());

        notificationRepository.save(notification);
    }

    @Override
    public BasicRestResponse readNotification(Integer id, HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        try {

            final Notification notification = notificationRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("No notification found with the given id: %1$s", id)));

            notification.setRead(true);

            notificationRepository.save(notification);

            response.setMessage(String.format("Notification with the id: %1$s updated successfully", id));
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }
}