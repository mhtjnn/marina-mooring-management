package com.marinamooringmanagement.utils;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.entity.WorkOrder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class FilterUtils {

    public Boolean filterWorkOrderDatesNoBasic(final WorkOrder workOrder, final LocalDate localGivenScheduleDate, final LocalDate localGivenDueDate) {
        LocalDate localSavedScheduleDate = workOrder.getScheduledDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate localSavedDueDate = workOrder.getDueDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return localSavedScheduleDate.isAfter(localGivenScheduleDate)
                && localSavedDueDate.isBefore(localGivenDueDate)
                && !localGivenScheduleDate.isAfter(localSavedDueDate)
                && !localGivenDueDate.isBefore(localSavedScheduleDate);
    }

    public Boolean filterWorkOrderBasic(final WorkOrder workOrder, final User user) {
        return null != workOrder.getTechnicianUser()
                && null != workOrder.getCustomerOwnerUser()
                && null != workOrder.getWorkOrderStatus()
                && null != workOrder.getWorkOrderStatus().getStatus()
                && workOrder.getCustomerOwnerUser().getId().equals(user.getId());
    }

    public Boolean filterWorkOrderDatesProvided(final WorkOrder workOrder, final User user, final LocalDate localGivenScheduleDate, final LocalDate localGivenDueDate) {
        return filterWorkOrderBasic(workOrder, user)
                && null != workOrder.getScheduledDate()
                && null != workOrder.getDueDate()
                && filterWorkOrderDatesNoBasic(workOrder, localGivenScheduleDate, localGivenDueDate);
    }

    public Boolean filterWorkOrderTechnicianProvidedDateProvided(final WorkOrder workOrder, final User technicianUser, final LocalDate localGivenScheduleDate, final LocalDate localGivenDueDate) {
        return null != workOrder.getTechnicianUser()
                && null != workOrder.getCustomerOwnerUser()
                && null != workOrder.getWorkOrderStatus()
                && null != workOrder.getWorkOrderStatus().getStatus()
                && StringUtils.equals(workOrder.getWorkOrderStatus().getStatus(), AppConstants.WorkOrderStatusConstants.CLOSE)
                && workOrder.getCustomerOwnerUser().getId().equals(technicianUser.getCustomerOwnerId())
                && workOrder.getTechnicianUser().getId().equals(technicianUser.getId())
                && null != workOrder.getScheduledDate()
                && null != workOrder.getDueDate()
                && filterWorkOrderDatesNoBasic(workOrder, localGivenScheduleDate, localGivenDueDate);
    }

    public Boolean filterWorkOrderTechnicianProvided(final WorkOrder workOrder, final User technicianUser) {
        return null != workOrder.getTechnicianUser()
                && null != workOrder.getCustomerOwnerUser()
                && null != workOrder.getWorkOrderStatus()
                && null != workOrder.getWorkOrderStatus().getStatus()
                && !StringUtils.equals(workOrder.getWorkOrderStatus().getStatus(), AppConstants.WorkOrderStatusConstants.CLOSE)
                && workOrder.getTechnicianUser().getId().equals(technicianUser.getId())
                && workOrder.getCustomerOwnerUser().getId().equals(technicianUser.getCustomerOwnerId());
    }

}
