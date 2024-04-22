package com.marinamooringmanagement;

import com.marinamooringmanagement.mapper.WorkOrderMapper;
import com.marinamooringmanagement.model.dto.WorkOrderDto;
import com.marinamooringmanagement.model.entity.WorkOrder;
import com.marinamooringmanagement.model.request.WorkOrderRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.repositories.WorkOrderRepository;
import com.marinamooringmanagement.service.impl.WorkOrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;



import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class WorkOrderTests {
    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private WorkOrderMapper workOrderMapper;

    @InjectMocks
    private WorkOrderServiceImpl workOrderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void saveWorkOrder_ValidRequest_Success() {
        WorkOrderRequestDto requestDto = new WorkOrderRequestDto();
        requestDto.setCustomerName("John Doe");

        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(1);
        workOrder.setCustomerName("John Doe");
        when(workOrderMapper.mapToWorkOrder(any(), any())).thenReturn(workOrder);
        when(workOrderRepository.save(any())).thenReturn(workOrder);

        BasicRestResponse response = workOrderService.saveWorkOrder(requestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals("Customer saved successfully", response.getMessage());
    }

//    @Test
//    public void getWorkOrders_ValidRequest_Success() {
//        int pageNumber = 0;
//        int pageSize = 10;
//        String sortBy = "status";
//        String sortDir = "asc";
//
//        List<WorkOrder> workOrders = new ArrayList<>();
//        WorkOrder workOrder = new WorkOrder();
//        workOrder.setId(2);
//        workOrder.setCustomerName("shiprakinger");
//        workOrders.add(workOrder);
//
//        when(workOrderRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(workOrders));
//
//        WorkOrderDto expectedDto = new WorkOrderDto();
//        expectedDto.setCustomerName("shiprakinger");
//        when(workOrderMapper.toDto(any(WorkOrder.class))).thenReturn(expectedDto);
//
//        List<WorkOrderDto> result = workOrderService.getWorkOrders(pageNumber, pageSize, sortBy, sortDir);
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals("shiprakinger", result.get(0).getCustomerName());
//    }

//    @Test
//    public void getbyId_ValidId_Success() {
//        Long id = 1L;
//        WorkOrder workOrder = new WorkOrder();
//        workOrder.setId(id);
//        workOrder.setCustomerName("John Doe");
//        when(workOrderRepository.findById(id)).thenReturn(Optional.of(workOrder));
//
//        WorkOrderDto result = workOrderService.getbyId(id.intValue());
//
//        assertNotNull(result);
//        assertEquals("John Doe", result.getCustomerName());
//    }

}
