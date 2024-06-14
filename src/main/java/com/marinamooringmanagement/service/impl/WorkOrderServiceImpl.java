package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.*;
import com.marinamooringmanagement.model.dto.WorkOrderStatusDto;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.WorkOrderRequestDto;
import com.marinamooringmanagement.model.response.*;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.service.WorkOrderService;
import com.marinamooringmanagement.utils.SortUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkOrderServiceImpl implements WorkOrderService {

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private SortUtils sortUtils;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private WorkOrderMapper workOrderMapper;

    @Autowired
    private MooringMapper mooringMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private BoatyardMapper boatyardMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkOrderStatusRepository workOrderStatusRepository;

    @Autowired
    private MooringRepository mooringRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkOrderStatusMapper workOrderStatusMapper;

    private static final Logger log = LoggerFactory.getLogger(WorkOrderServiceImpl.class);

    @Override
    public BasicRestResponse fetchWorkOrders(BaseSearchRequest baseSearchRequest, String searchText, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to fetch all the moorings in the database");

            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

            Specification<WorkOrder> spec = new Specification<WorkOrder>() {
                @Override
                public Predicate toPredicate(Root<WorkOrder> workOrder, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();

                    if (null != searchText) {
                        String lowerCaseSearchText = "%" + searchText.toLowerCase() + "%";
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(workOrder.get("problem"), "%" + lowerCaseSearchText + "%"),
                                criteriaBuilder.like(workOrder.join("mooring").join("customer").get("firstName"), "%" + lowerCaseSearchText + "%"),
                                criteriaBuilder.like(workOrder.join("mooring").join("customer").get("lastName"), "%" + lowerCaseSearchText + "%"),
                                criteriaBuilder.like(workOrder.join("mooring").get("mooringId"), "%" + lowerCaseSearchText + "%"),
                                criteriaBuilder.like(workOrder.join("mooring").join("boatyard").get("boatyardName"), "%" + lowerCaseSearchText + "%"),
                                criteriaBuilder.like(workOrder.join("technicianUser").get("name"), "%" + lowerCaseSearchText + "%")
                        ));
                    }

                    predicates.add(authorizationUtil.fetchPredicateForWorkOrder(customerOwnerId, workOrder, criteriaBuilder));

                    return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };

            final Pageable pageable = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    sortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir()));

            final Page<WorkOrder> workOrderList = workOrderRepository.findAll(spec, pageable);

            final List<WorkOrderResponseDto> workOrderResponseDtoList = workOrderList
                    .getContent()
                    .stream()
                    .map(workOrder -> {
                        WorkOrderResponseDto workOrderResponseDto = workOrderMapper.mapToWorkOrderResponseDto(WorkOrderResponseDto.builder().build(), workOrder);
                        if(null != workOrder.getMooring()) workOrderResponseDto.setMooringResponseDto(mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), workOrder.getMooring()));
                        if(null != workOrder.getMooring() && null != workOrder.getMooring().getCustomer()) workOrderResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), workOrder.getMooring().getCustomer()));
                        if(null != workOrder.getMooring() && null != workOrder.getMooring().getBoatyard()) workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), workOrder.getMooring().getBoatyard()));
                        if(null != workOrder.getCustomerOwnerUser()) workOrderResponseDto.setCustomerOwnerUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getCustomerOwnerUser()));
                        if(null != workOrder.getTechnicianUser()) workOrderResponseDto.setTechnicianUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getTechnicianUser()));
                        if(null != workOrder.getWorkOrderStatus()) workOrderResponseDto.setWorkOrderStatusDto(workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), workOrder.getWorkOrderStatus()));
                        if(null != workOrder.getDueDate()) {
                            LocalDate dueDate = workOrder.getDueDate()
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();

                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String dueDateStr = dueDate.format(dateTimeFormatter);
                            workOrderResponseDto.setDueDate(dueDateStr);
                        }

                        if(null != workOrder.getScheduledDate()) {
                            LocalDate scheduledDate = workOrder.getScheduledDate()
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();

                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String scheduleDateStr = scheduledDate.format(dateTimeFormatter);
                            workOrderResponseDto.setScheduledDate(scheduleDateStr);
                        }

                        return workOrderResponseDto;
                    })
                    .collect(Collectors.toList());

            response.setMessage("All work orders fetched successfully.");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(workOrderResponseDtoList);
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse saveWorkOrder(WorkOrderRequestDto workOrderRequestDto, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to save the work order in the database");
            final WorkOrder workOrder = WorkOrder.builder().build();

            if(null == workOrderRequestDto.getTechnicianId()) throw new RuntimeException("Technician Id cannot be null");
            if(null == workOrderRequestDto.getMooringId()) throw new RuntimeException("Mooring Id cannot be null");

            performSave(workOrderRequestDto, workOrder, null, request);

            response.setMessage("Work order saved successfully.");
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            log.error("Error occurred while saving the work order in the database {}", e.getLocalizedMessage());
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse updateWorkOrder(WorkOrderRequestDto workOrderRequestDto, Integer workOrderId, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info("API called to update the mooring with the given mooring ID");
            if (workOrderId == null) {
                throw new IllegalArgumentException("Work order Id not provided for update request");
            }
            Optional<WorkOrder> optionalWorkOrder = workOrderRepository.findById(workOrderId);
            final WorkOrder workOrder = optionalWorkOrder.orElseThrow(() -> new ResourceNotFoundException(String.format("Work order not found with id: %1$s", workOrderId)));
            performSave(workOrderRequestDto, workOrder, workOrderId, request);
            response.setMessage("Work order updated successfully");
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("Error occurred while updating work order {}", e.getLocalizedMessage());
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse deleteWorkOrder(final Integer workOrderId, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            if(-1 == customerOwnerId && null != request.getAttribute("CUSTOMER_OWNER_ID")) customerOwnerId = (Integer) request.getAttribute("CUSTOMER_OWNER_ID");

            Optional<WorkOrder> optionalWorkOrder = workOrderRepository.findById(workOrderId);
            if (optionalWorkOrder.isEmpty()) throw new RuntimeException(String.format("No work order exists with %1$s", workOrderId));
            final WorkOrder savedWorkOrder = optionalWorkOrder.get();

            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            if(null != savedWorkOrder.getCustomerOwnerUser()) {
                if(!savedWorkOrder.getCustomerOwnerUser().getId().equals(user.getId())) throw new RuntimeException(String.format("Work order with the id: %1$s is associated with some other customer owner", workOrderId));
            } else {
                throw new RuntimeException(String.format("Work order with the id: %1$s is not associated with any customer owner", workOrderId));
            }

            workOrderRepository.delete(savedWorkOrder);

            Optional<WorkOrder> optionalWorkOrderAfterDeletion = workOrderRepository.findById(workOrderId);

            final String message = optionalWorkOrderAfterDeletion.isEmpty() ? String.format("Work order with id %1$s deleted successfully", workOrderId) : String.format("Failed to delete work order with the given id %1$s", workOrderId);
            response.setMessage(message);
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("Error occurred while deleting work order with id " + workOrderId, e);
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse fetchOpenWorkOrders(Integer technicianId, HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final User technicianUser = authorizationUtil.checkForTechnician(technicianId, request);

            List<WorkOrderResponseDto> workOrderResponseDtoList = workOrderRepository.findAll()
                    .stream()
                    .filter(
                            workOrder -> null != workOrder.getTechnicianUser()
                                    && null != workOrder.getCustomerOwnerUser()
                                    && null != workOrder.getWorkOrderStatus()
                                    && null != workOrder.getWorkOrderStatus().getStatus()
                                    && !StringUtils.equals(workOrder.getWorkOrderStatus().getStatus(),AppConstants.WorkOrderStatusConstants.CLOSE)
                                    && workOrder.getTechnicianUser().getId().equals(technicianUser.getId())
                                    && workOrder.getCustomerOwnerUser().getId().equals(technicianUser.getCustomerOwnerId())
                    )
                    .map(workOrder -> {
                        WorkOrderResponseDto workOrderResponseDto = workOrderMapper.mapToWorkOrderResponseDto(WorkOrderResponseDto.builder().build(), workOrder);
                        if(null != workOrder.getMooring()) workOrderResponseDto.setMooringResponseDto(mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), workOrder.getMooring()));
                        if(null != workOrder.getMooring() && null != workOrder.getMooring().getCustomer()) workOrderResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), workOrder.getMooring().getCustomer()));
                        if(null != workOrder.getMooring() && null != workOrder.getMooring().getBoatyard()) workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), workOrder.getMooring().getBoatyard()));
                        if(null != workOrder.getCustomerOwnerUser()) workOrderResponseDto.setCustomerOwnerUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getCustomerOwnerUser()));
                        if(null != workOrder.getTechnicianUser()) workOrderResponseDto.setTechnicianUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getTechnicianUser()));
                        if(null != workOrder.getWorkOrderStatus()) workOrderResponseDto.setWorkOrderStatusDto(workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), workOrder.getWorkOrderStatus()));
                        if(null != workOrder.getDueDate()) {
                            LocalDate dueDate = workOrder.getDueDate()
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();

                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String dueDateStr = dueDate.format(dateTimeFormatter);
                            workOrderResponseDto.setDueDate(dueDateStr);
                        }

                        if(null != workOrder.getScheduledDate()) {
                            LocalDate scheduledDate = workOrder.getScheduledDate()
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();

                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String scheduleDateStr = scheduledDate.format(dateTimeFormatter);
                            workOrderResponseDto.setScheduledDate(scheduleDateStr);
                        }

                        return workOrderResponseDto;
                    })
                    .toList();

            response.setMessage(String.format("Work orders with technician of given id: %1$s fetched successfully", technicianId));
            response.setStatus(HttpStatus.OK.value());
            response.setContent(workOrderResponseDtoList);
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return  response;
    }

    @Override
    public BasicRestResponse fetchCloseWorkOrders(Integer technicianId, HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final User technicianUser = authorizationUtil.checkForTechnician(technicianId, request);

            List<WorkOrderResponseDto> workOrderResponseDtoList = workOrderRepository.findAll()
                    .stream()
                    .filter(
                            workOrder -> null != workOrder.getTechnicianUser()
                                    && null != workOrder.getCustomerOwnerUser()
                                    && null != workOrder.getWorkOrderStatus()
                                    && null != workOrder.getWorkOrderStatus().getStatus()
                                    && StringUtils.equals(workOrder.getWorkOrderStatus().getStatus(),AppConstants.WorkOrderStatusConstants.CLOSE)
                                    && workOrder.getTechnicianUser().getId().equals(technicianUser.getId())
                                    && workOrder.getCustomerOwnerUser().getId().equals(technicianUser.getCustomerOwnerId())
                    )
                    .map(workOrder -> {
                        WorkOrderResponseDto workOrderResponseDto = workOrderMapper.mapToWorkOrderResponseDto(WorkOrderResponseDto.builder().build(), workOrder);
                        if(null != workOrder.getMooring()) workOrderResponseDto.setMooringResponseDto(mooringMapper.mapToMooringResponseDto(MooringResponseDto.builder().build(), workOrder.getMooring()));
                        if(null != workOrder.getMooring() && null != workOrder.getMooring().getCustomer()) workOrderResponseDto.setCustomerResponseDto(customerMapper.mapToCustomerResponseDto(CustomerResponseDto.builder().build(), workOrder.getMooring().getCustomer()));
                        if(null != workOrder.getMooring() && null != workOrder.getMooring().getBoatyard()) workOrderResponseDto.setBoatyardResponseDto(boatyardMapper.mapToBoatYardResponseDto(BoatyardResponseDto.builder().build(), workOrder.getMooring().getBoatyard()));
                        if(null != workOrder.getCustomerOwnerUser()) workOrderResponseDto.setCustomerOwnerUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getCustomerOwnerUser()));
                        if(null != workOrder.getTechnicianUser()) workOrderResponseDto.setTechnicianUserResponseDto(userMapper.mapToUserResponseDto(UserResponseDto.builder().build(), workOrder.getTechnicianUser()));
                        if(null != workOrder.getWorkOrderStatus()) workOrderResponseDto.setWorkOrderStatusDto(workOrderStatusMapper.mapToDto(WorkOrderStatusDto.builder().build(), workOrder.getWorkOrderStatus()));
                        if(null != workOrder.getDueDate()) {
                            LocalDate dueDate = workOrder.getDueDate()
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();

                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String dueDateStr = dueDate.format(dateTimeFormatter);
                            workOrderResponseDto.setDueDate(dueDateStr);
                        }

                        if(null != workOrder.getScheduledDate()) {
                            LocalDate scheduledDate = workOrder.getScheduledDate()
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();

                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            String scheduleDateStr = scheduledDate.format(dateTimeFormatter);
                            workOrderResponseDto.setScheduledDate(scheduleDateStr);
                        }
                        return workOrderResponseDto;
                    })
                    .toList();

            response.setMessage(String.format("Work orders with technician of given id: %1$s fetched successfully", technicianId));
            response.setStatus(HttpStatus.OK.value());
            response.setContent(workOrderResponseDtoList);
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return  response;
    }

    private void performSave(final WorkOrderRequestDto workOrderRequestDto, final WorkOrder workOrder, final Integer workOrderId, final HttpServletRequest request) {
        try {
            if(null == workOrderId) workOrder.setLastModifiedDate(new Date(System.currentTimeMillis()));

            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            workOrder.setCustomerOwnerUser(user);

            workOrderMapper.mapToWorkOrder(workOrder, workOrderRequestDto);

            if(null != workOrderRequestDto.getDueDate()) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate localDate = LocalDate.parse(workOrderRequestDto.getDueDate(), dateTimeFormatter);
                Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

                workOrder.setDueDate(date);
            } else {
                if(workOrderId == null) throw new RuntimeException(String.format("Due date cannot be null"));
            }

            if(null != workOrderRequestDto.getScheduledDate()) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate localDate = LocalDate.parse(workOrderRequestDto.getScheduledDate(), dateTimeFormatter);
                Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

                workOrder.setScheduledDate(date);
            } else {
                if(workOrderId == null) throw new RuntimeException(String.format("Due date cannot be null"));
            }

            if(null != workOrderRequestDto.getWorkOrderStatusId()) {
                final Optional<WorkOrderStatus> optionalWorkOrderStatus = workOrderStatusRepository.findById(workOrderRequestDto.getWorkOrderStatusId());
                if(optionalWorkOrderStatus.isEmpty()) throw new RuntimeException(String.format("No work order status found with the given id: %1$s", workOrderRequestDto.getWorkOrderStatusId()));

                final WorkOrderStatus workOrderStatus = optionalWorkOrderStatus.get();
                workOrder.setWorkOrderStatus(workOrderStatus);
            } else {
                if(null == workOrderId) throw new RuntimeException(String.format("While saving work order status cannot be null"));
            }

            if(null != workOrderRequestDto.getMooringId()) {
                Optional<Mooring> optionalMooring = mooringRepository.findById(workOrderRequestDto.getMooringId());
                if(optionalMooring.isEmpty()) throw new RuntimeException(String.format("No mooring found with the given id: %1$s", workOrderRequestDto.getMooringId()));

                final Mooring mooring = optionalMooring.get();

                if(null == workOrderRequestDto.getCustomerId()) throw new RuntimeException("Customer Id cannot be null during saving/updating work order");
                if(null == workOrderRequestDto.getBoatyardId()) throw new RuntimeException("Boatyard Customer Id cannot be null during saving/updating work order");
                if(null == mooring.getCustomer()) throw new RuntimeException(String.format("No customer found for the mooring with the id: %1$s", workOrderRequestDto.getMooringId()));
                if(null == mooring.getBoatyard()) throw new RuntimeException(String.format("No boatyard found for the mooring with the id: %1$s", workOrderRequestDto.getBoatyardId()));
                if(!mooring.getCustomer().getId().equals(workOrderRequestDto.getCustomerId())) throw new RuntimeException(String.format("Customer Id is different in mooring with given id: %1$s", mooring.getId()));
                if(!mooring.getBoatyard().getId().equals(workOrderRequestDto.getBoatyardId())) throw new RuntimeException(String.format("Boatyard Id is different in mooring with given id: %1$s", mooring.getId()));

                workOrder.setMooring(mooring);
            } else {
                if(null == workOrderId) throw new RuntimeException(String.format("While saving work order mooring id cannot be null"));
            }

            if(null != workOrderRequestDto.getTechnicianId()) {
                Optional<User> optionalTechnician = userRepository.findById(workOrderRequestDto.getTechnicianId());
                if(optionalTechnician.isEmpty()) throw new RuntimeException(String.format("No technician found with the given id: %1$s", workOrderRequestDto.getTechnicianId()));

                final User technician = optionalTechnician.get();

                if(null == technician.getRole()) throw new RuntimeException(String.format("User with id: %1$s is not assigned to any role", technician.getId()));

                if(!technician.getRole().getName().equals(AppConstants.Role.TECHNICIAN)) throw new RuntimeException(String.format("User with the id: %1$s is not of technician role", technician.getId()));

                workOrder.setTechnicianUser(technician);
            } else {
                if(null == workOrderId) throw new RuntimeException(String.format("Technician Id cannot be null during saving/updating work order"));
            }

             workOrderRepository.save(workOrder);
        } catch (Exception e) {
            log.error("Error occurred during performSave() function {}", e.getLocalizedMessage());
            throw new DBOperationException(e.getMessage(), e);
        }
    }
}
