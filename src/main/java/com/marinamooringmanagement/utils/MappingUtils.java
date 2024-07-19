package com.marinamooringmanagement.utils;

import com.marinamooringmanagement.mapper.*;
import com.marinamooringmanagement.mapper.metadata.WorkOrderStatusMapper;
import com.marinamooringmanagement.model.dto.metadata.WorkOrderStatusDto;
import com.marinamooringmanagement.model.entity.WorkOrder;
import com.marinamooringmanagement.model.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MappingUtils {

    @Autowired
    private WorkOrderMapper workOrderMapper;

    @Autowired
    private WorkOrderStatusMapper workOrderStatusMapper;

    @Autowired
    private MooringMapper mooringMapper;

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BoatyardMapper boatyardMapper;

    public WorkOrderResponseDto mapToWorkOrderResponseDto(final WorkOrder workOrder) {
        WorkOrderResponseDto workOrderResponseDto = workOrderMapper.mapToWorkOrderResponseDto(WorkOrderResponseDto.builder().build(), workOrder);
        workOrderResponseDto.setWorkOrderStatusDto(workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), workOrder.getWorkOrderStatus()));
        MooringResponseDto mooringResponseDto = MooringResponseDto.builder().build();
        if (null != workOrder.getMooring()) {
            mooringMapper.mapToMooringResponseDto(mooringResponseDto, workOrder.getMooring());
            if (null != workOrder.getMooring().getInstallConditionOfEyeDate()) {
                mooringResponseDto.setInstallConditionOfEyeDate(dateUtil.dateToString(workOrder.getMooring().getInstallConditionOfEyeDate()));
            }
            if (null != workOrder.getMooring().getInstallTopChainDate()) {
                mooringResponseDto.setInstallTopChainDate(dateUtil.dateToString(workOrder.getMooring().getInstallTopChainDate()));
            }
            if (null != workOrder.getMooring().getInstallBottomChainDate()) {
                mooringResponseDto.setInstallBottomChainDate(dateUtil.dateToString(workOrder.getMooring().getInstallBottomChainDate()));
            }
            workOrderResponseDto.setMooringResponseDto(mooringResponseDto);
        }
        if (null != workOrder.getMooring() && null != workOrder.getMooring().getCustomer()) {
            workOrderResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), workOrder.getMooring().getCustomer()));
            if (null != workOrder.getMooring().getCustomer().getFirstName()
                    && null != workOrder.getMooring().getCustomer().getLastName())
                mooringResponseDto.setCustomerName(
                        workOrder.getMooring().getCustomer().getFirstName() + " " + workOrder.getMooring().getCustomer().getLastName()
                );
        }
        if (null != workOrder.getMooring() && null != workOrder.getMooring().getBoatyard())
            workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), workOrder.getMooring().getBoatyard()));
        if (null != workOrder.getCustomerOwnerUser())
            workOrderResponseDto.setCustomerOwnerUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getCustomerOwnerUser()));
        if (null != workOrder.getTechnicianUser())
            workOrderResponseDto.setTechnicianUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getTechnicianUser()));
        if (null != workOrder.getWorkOrderStatus())
            workOrderResponseDto.setWorkOrderStatusDto(workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), workOrder.getWorkOrderStatus()));
        if (null != workOrder.getDueDate())
            workOrderResponseDto.setDueDate(dateUtil.dateToString(workOrder.getDueDate()));
        if (null != workOrder.getScheduledDate())
            workOrderResponseDto.setScheduledDate(dateUtil.dateToString(workOrder.getScheduledDate()));

        return workOrderResponseDto;
    }

}
