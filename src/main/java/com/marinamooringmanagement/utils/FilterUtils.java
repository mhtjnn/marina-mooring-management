package com.marinamooringmanagement.utils;

import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.entity.WorkOrder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class FilterUtils {

    public Boolean filterWorkOrderDatesProvided(final WorkOrder workOrder, final User user, final LocalDate localGivenScheduleDate, final LocalDate localGivenDueDate) {
        if (null != workOrder.getTechnicianUser()
                && null != workOrder.getCustomerOwnerUser()
                && null != workOrder.getWorkOrderStatus()
                && null != workOrder.getWorkOrderStatus().getStatus()
                && workOrder.getCustomerOwnerUser().getId().equals(user.getId())
                && null != workOrder.getScheduledDate()
                && null != workOrder.getDueDate()) {

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
        return false;
    }

}
