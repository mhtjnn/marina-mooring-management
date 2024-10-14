package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.constants.enums.EntityEnum;
import com.marinamooringmanagement.model.entity.Notification;
import com.marinamooringmanagement.model.entity.WorkOrder;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.NotificationResponseDto;
import com.marinamooringmanagement.repositories.NotificationRepository;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public BasicRestResponse getNotifications(Integer id, BaseSearchRequest baseSearchRequest, String searchText, HttpServletRequest request) {

        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        try {
            Pageable pageable = PageRequest.of(baseSearchRequest.getPageNumber(), baseSearchRequest.getPageSize());
            Page<NotificationResponseDto> notificationResponseDtoPage = notificationRepository.findAll(id, pageable);

            response.setContent(notificationResponseDtoPage);
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

        notification.setCreatedById(LoggedInUserUtil.getLoggedInUserID());
        notification.setSendToId(workOrder.getTechnicianUser().getId());
        notification.setNotificationMessage(String.format(AppConstants.NotificationMessageConstants.UPDATED, EntityEnum.WORK_ORDER.getEntityType(), workOrder.getMooring().getMooringStatus().getStatus()));
        notification.setRead(false);
        notification.setEntityStr(EntityEnum.WORK_ORDER.getEntityType());
        notification.setEntityId(workOrder.getId());

        notificationRepository.save(notification);
    }
}
