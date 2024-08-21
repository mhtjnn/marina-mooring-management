package com.marinamooringmanagement.scheduler;


import com.marinamooringmanagement.model.entity.WorkOrder;
import com.marinamooringmanagement.repositories.WorkOrderRepository;
import com.marinamooringmanagement.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class WorkOrderDueScheduler {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private EmailService emailService;

    private static final Logger log = LoggerFactory.getLogger(WorkOrderDueScheduler.class);

    @Scheduled(cron = "${technician.update.cron}")
    public void notifyTechnician() {
        try {
            LocalDate currentDate = LocalDate.now(); // Get the current date
            log.info(String.format("Notifying technician/s prior, date: %1$s", currentDate));

            LocalDate dateAfter30Days = currentDate.plusDays(30);

            final Date after30DaysDate = Date.from(dateAfter30Days.atStartOfDay(ZoneId.systemDefault()).toInstant());

            List<WorkOrder> workOrderWithOpenWorkOrderWithDateNotification = workOrderRepository.findAllWorkOrderWithOpenWorkOrderWithDateNotification(after30DaysDate);

            for (WorkOrder workOrder : workOrderWithOpenWorkOrderWithDateNotification) {
                log.info(String.format("Sending Email for work order of number: %1%s", workOrder.getWorkOrderNumber()));
                emailService.sendNotificationForWorkOrder(workOrder);
                log.info(String.format("Email sended successfully for work order of number: %1%s", workOrder.getWorkOrderNumber()));
            }

            log.info(String.format("Technician/s notified for date due date as: %1$s", dateAfter30Days));
        } catch (Exception e) {
            log.info(String.format("Error occurred during notifying technician: %1$s", e.getLocalizedMessage()));
            throw e;
        }
    }

}
